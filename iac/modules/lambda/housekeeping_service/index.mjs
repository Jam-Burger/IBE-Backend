import 'dotenv/config';
import pkg from 'pg';

import {getBookedRooms, getCheckingInRooms, getCheckingOutRooms} from './services/graphqlClient.mjs';
import {getTaskTypeDurations} from './services/cleaningCalculator.mjs';
import {
    getAvailableStaffForDate,
    getAvailableStaffPerShift,
    getCheckinCheckoutTime,
    getShiftsBeforeCheckIn,
    getShiftsByProperty,
    getStaffByShiftIds,
    getTemporaryStaff,
    getTemporaryStaffForShifts,
} from './services/staffAvailabilityService.mjs';

import {
    getRoomsCheckedInOnly,
    getRoomsCheckedOutOnly,
    getRoomsWithNoCheckInOrOut,
    getRoomsWithSameCheckInAndOut,
} from './utils/roomCategorizer.mjs';
import {assignCleaningTasks, deleteReport, insertTasks} from './services/taskScheduler.mjs';
import {timeStrToMinutes, toIsoString} from './utils/timeConverter.mjs';
import {sendEmails} from "./services/emailService.mjs";

const {Pool} = pkg;

// Enable X-Ray tracing for PostgreSQL
const pgPool = new Pool({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    database: process.env.DB_NAME,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
});

export const handler = async (event) => {
    const today = toIsoString(new Date());
    let client;
    try {
        // Create subsegment for database connection
        client = await pgPool.connect();

        console.log("Deleting old tasks...");
        await deleteReport(client, today);
        console.log("Old tasks deleted");

        for (let propertyId = 9; propertyId <= 9; propertyId++) {
            const {checkInTime, checkOutTime} = await getCheckinCheckoutTime(client, propertyId);
            const checkInMinutes = timeStrToMinutes(checkInTime);
            const checkOutMinutes = timeStrToMinutes(checkOutTime);

            const bookedRooms = await getBookedRooms(propertyId, today);
            const checkingInRoomsToday = await getCheckingInRooms(propertyId, today);
            const checkingOutRoomsToday = await getCheckingOutRooms(propertyId, today);

            if (!bookedRooms || !checkingInRoomsToday || !checkingOutRoomsToday) {
                console.error("❌ Error fetching data from GraphQL API");
                return;
            }

            const sameDayRooms = getRoomsWithSameCheckInAndOut(checkingInRoomsToday, checkingOutRoomsToday);
            const checkoutOnlyRooms = getRoomsCheckedOutOnly(checkingOutRoomsToday, checkingInRoomsToday);
            const checkinOnlyRooms = getRoomsCheckedInOnly(checkingInRoomsToday, checkingOutRoomsToday);
            const noActivityRooms = getRoomsWithNoCheckInOrOut(bookedRooms, checkingInRoomsToday, checkingOutRoomsToday);

            const taskTypeDurations = await getTaskTypeDurations(client);

            // Calculate total cleaning times by type
            const totalCheckoutCleaningTime = (sameDayRooms.size + checkoutOnlyRooms.size) * taskTypeDurations.checkout;
            const totalCheckinCleaningTime = checkinOnlyRooms.size * taskTypeDurations.checkin;
            const totalDailyCleaningTime = noActivityRooms.size * taskTypeDurations.daily;

            // Total cleaning time needed
            const totalCleaningTime = totalCheckoutCleaningTime + totalCheckinCleaningTime + totalDailyCleaningTime;

            // Cleaning time needed before check-in (only same-day and check-in rooms)
            const beforeCheckInCleaningTime = (sameDayRooms.size * taskTypeDurations.checkout) +
                (checkinOnlyRooms.size * taskTypeDurations.checkin);

            // Calculate time needed between checkout and checkin for same-day rooms
            const checkoutToCheckinWindow = checkInMinutes - checkOutMinutes;

            // For same-day rooms, we need to clean after checkout and before checkin
            const sameDayCleaningTime = sameDayRooms.size * taskTypeDurations.checkout;

            // Calculate how many staff we need specifically for the checkout-to-checkin window
            const criticalWindowStaffNeeded = Math.ceil(sameDayCleaningTime / checkoutToCheckinWindow);

            const availableStaffData = await getAvailableStaffPerShift(client, propertyId);
            const shifts = await getShiftsByProperty(client, propertyId);

            let availableBeforeCheckInMinutes = 0;
            let availableAfterCheckoutMinutes = 0;
            let totalAvailableMinutes = 0;

            for (const row of availableStaffData) {
                const staffCount = parseInt(row.available_staff_count || 0, 10);
                const shiftStart = timeStrToMinutes(row.shift_start);
                const shiftEnd = timeStrToMinutes(row.shift_end);
                const shiftDuration = shiftEnd - shiftStart;

                totalAvailableMinutes += staffCount * shiftDuration;

                // Calculate minutes available after checkout time
                const minutesAfterCheckout = Math.max(0, Math.min(shiftEnd, checkInMinutes) - Math.max(shiftStart, checkOutMinutes));
                availableAfterCheckoutMinutes += staffCount * minutesAfterCheckout;

                // Calculate minutes available before check-in
                const minutesBeforeCheckIn = Math.max(0, Math.min(shiftEnd, checkInMinutes) - shiftStart);
                availableBeforeCheckInMinutes += staffCount * minutesBeforeCheckIn;
            }

            const remainingBeforeCheckIn = beforeCheckInCleaningTime - availableBeforeCheckInMinutes;
            const remainingAfterCheckout = sameDayCleaningTime - availableAfterCheckoutMinutes;
            const remainingTotal = totalCleaningTime - totalAvailableMinutes;

            const bottleneckShiftGeneral = shifts.reduce((min, shift) => {
                const start = timeStrToMinutes(shift.start_time);
                const end = timeStrToMinutes(shift.end_time);
                const dur = end - start;
                return dur < min ? dur : min;
            }, Infinity);

            const shiftsBeforeCheckIn = getShiftsBeforeCheckIn(shifts, checkInMinutes);

            const bottleneckShiftBeforeCheckIn = shiftsBeforeCheckIn.reduce((min, shift) => {
                const start = timeStrToMinutes(shift.start_time);
                const end = timeStrToMinutes(shift.end_time);
                const dur = end - start;
                return dur < min ? dur : min;
            }, Infinity);

            // Calculate shifts that overlap the critical checkout-to-checkin window
            const shiftsInCriticalWindow = shifts.filter(shift => {
                const start = timeStrToMinutes(shift.start_time);
                const end = timeStrToMinutes(shift.end_time);
                return start < checkInMinutes && end > checkOutMinutes;
            });

            // Find the shortest shift that covers the critical window
            const bottleneckShiftCriticalWindow = shiftsInCriticalWindow.reduce((min, shift) => {
                const start = timeStrToMinutes(shift.start_time);
                const end = timeStrToMinutes(shift.end_time);
                const dur = end - start;
                return dur < min ? dur : min;
            }, Infinity);

            let additionalBeforeCheckInStaff = 0;
            let additionalAfterCheckoutStaff = 0;
            let additionalGeneralStaff = 0;

            if (remainingBeforeCheckIn > 0 && isFinite(bottleneckShiftBeforeCheckIn) && bottleneckShiftBeforeCheckIn > 0) {
                additionalBeforeCheckInStaff = Math.ceil(remainingBeforeCheckIn / bottleneckShiftBeforeCheckIn)
            }

            // Calculate additional staff needed specifically for the critical window
            if (remainingAfterCheckout > 0 && isFinite(bottleneckShiftCriticalWindow) && bottleneckShiftCriticalWindow > 0) {
                additionalAfterCheckoutStaff = Math.ceil(remainingAfterCheckout / bottleneckShiftCriticalWindow);
            }

            if (remainingTotal > 0 && isFinite(bottleneckShiftGeneral) && bottleneckShiftGeneral > 0) {
                additionalGeneralStaff = Math.ceil(remainingTotal / bottleneckShiftGeneral);
            }

            // Calculate the specific critical window staff needs
            const directCalculatedCriticalStaff = Math.max(0, criticalWindowStaffNeeded - shiftsInCriticalWindow.length);

            // Take the maximum of all staff calculations to ensure we have enough
            const additionalStaffNeeded = Math.max(
                additionalBeforeCheckInStaff,
                additionalAfterCheckoutStaff,
                additionalGeneralStaff,
                directCalculatedCriticalStaff
            );

            const staff = await getStaffByShiftIds(client, shifts.map(s => s.id));
            const availability = await getAvailableStaffForDate(client, propertyId, today.split('T')[0]);

            let finalStaff = staff;
            let tempCriticalStaff = [];
            let tempBeforeCheckInStaff = [];
            let tempGeneralStaff = [];

            if (additionalStaffNeeded > 0) {
                console.log(`\n🚨 Additional temporary staff required: ${additionalStaffNeeded}`);

                // Prioritize shifts that cover the critical window
                const criticalShiftIds = shiftsInCriticalWindow.map(s => s.id);

                if (criticalShiftIds.length > 0) {
                    tempCriticalStaff = await getTemporaryStaffForShifts(
                        client,
                        criticalShiftIds,
                        Math.min(additionalAfterCheckoutStaff, additionalStaffNeeded)
                    );
                    console.log(`  - Assigned to critical window: ${tempCriticalStaff.length}`);
                }

                const remainingCount = additionalStaffNeeded - tempCriticalStaff.length;

                // Then try to get staff for shifts before check-in
                const shiftIdsBeforeCheckIn = shiftsBeforeCheckIn.map(s => s.id);

                if (remainingCount > 0 && shiftIdsBeforeCheckIn.length > 0) {
                    tempBeforeCheckInStaff = await getTemporaryStaffForShifts(
                        client,
                        shiftIdsBeforeCheckIn,
                        Math.min(additionalBeforeCheckInStaff, remainingCount)
                    );
                    console.log(`  - Assigned to before check-in shifts: ${tempBeforeCheckInStaff.length}`);
                }

                // Finally, get general staff if still needed
                const finalRemainingCount = additionalStaffNeeded - tempCriticalStaff.length - tempBeforeCheckInStaff.length;

                if (finalRemainingCount > 0) {
                    tempGeneralStaff = await getTemporaryStaff(client, finalRemainingCount);
                    console.log(`  - Assigned to general shifts: ${tempGeneralStaff.length}`);
                }

                const tempStaff = [...tempCriticalStaff, ...tempBeforeCheckInStaff, ...tempGeneralStaff];
                finalStaff = [...staff, ...tempStaff];

                // Log details about temporary staff
                console.log(`\n📊 Temporary Staff Breakdown:`);
                console.log(`  - Permanent staff: ${staff.length}`);
                console.log(`  - Temporary staff: ${tempStaff.length}`);
                console.log(`    > For critical window: ${tempCriticalStaff.length}`);
                console.log(`    > For before check-in: ${tempBeforeCheckInStaff.length}`);
                console.log(`    > For general tasks: ${tempGeneralStaff.length}`);
                console.log(`  - Total staff assigned: ${finalStaff.length}`);
            } else {
                console.log(`\n✅ Enough staff to complete cleaning before check-in!`);
                console.log(`  - Total permanent staff: ${staff.length}`);
            }

            console.log("\nAssigning cleaning tasks...");
            const tasks = assignCleaningTasks({
                date: today,
                checkInMinutes,
                checkOutMinutes,
                sameDayRooms,
                checkoutOnlyRooms,
                checkinOnlyRooms,
                noActivityRooms,
                staff: finalStaff,
                shifts,
                availability,
                durations: taskTypeDurations
            });

            console.log("Inserting tasks...");
            await insertTasks(client, tasks);
            console.log("Task insertion complete");

            // 🧼 Cleaning Summary
            console.log("\n📋 Cleaning Summary:");
            console.log(`Same-day check-in & check-out: ${sameDayRooms.size}`);
            console.log(`Checkout only: ${checkoutOnlyRooms.size}`);
            console.log(`Checkin only: ${checkinOnlyRooms.size}`);
            console.log(`No activity: ${noActivityRooms.size}`);
            console.log(`Total cleaning time (min): ${totalCleaningTime}`);
            console.log(`Cleaning before check-in time: ${beforeCheckInCleaningTime}`);
            console.log(`Critical window cleaning time (checkout to checkin): ${sameDayCleaningTime}`);
            console.log(`\nTotal available staff minutes: ${totalAvailableMinutes}`);
            console.log(`Available minutes before check-in: ${availableBeforeCheckInMinutes}`);
            console.log(`Available minutes in critical window: ${availableAfterCheckoutMinutes}`);
            console.log(`Remaining before check-in minutes: ${remainingBeforeCheckIn}`);
            console.log(`Remaining critical window minutes: ${remainingAfterCheckout}`);
            console.log(`Remaining total minutes: ${remainingTotal}`);
            console.log(`Direct calculated critical window staff: ${criticalWindowStaffNeeded}`);

            if (additionalStaffNeeded > 0) {
                console.log(`\n🚨 Staff Analysis:`);
                console.log(`  - Additional staff needed: ${additionalStaffNeeded}`);
                console.log(`  - Permanent staff: ${staff.length}`);
                console.log(`  - Temporary staff added: ${tempCriticalStaff.length + tempBeforeCheckInStaff.length + tempGeneralStaff.length}`);
                console.log(`    > Critical window: ${tempCriticalStaff.length}`);
                console.log(`    > Before check-in: ${tempBeforeCheckInStaff.length}`);
                console.log(`    > General: ${tempGeneralStaff.length}`);
                console.log(`  - Total staff assigned: ${finalStaff.length}`);
            } else {
                console.log(`\n✅ Staff Analysis:`);
                console.log(`  - No additional staff needed`);
                console.log(`  - Permanent staff: ${staff.length}`);
                console.log(`  - Total staff assigned: ${staff.length}`);
            }

            // Count tasks by type
            const tasksByType = {
                "immediate-checkout": 0,
                "checkout": 0,
                "checkin": 0,
                "daily": 0
            };

            tasks.forEach(task => {
                if (tasksByType.hasOwnProperty(task.task_type)) {
                    tasksByType[task.task_type]++;
                }
            });

            console.log(`\n📝 Task Assignment Summary:`);
            console.log(`  - Total tasks assigned: ${tasks.length}`);
            console.log(`  - Immediate checkout tasks: ${tasksByType["immediate-checkout"]}`);
            console.log(`  - Regular checkout tasks: ${tasksByType["checkout"]}`);
            console.log(`  - Check-in tasks: ${tasksByType["checkin"]}`);
            console.log(`  - Daily cleaning tasks: ${tasksByType["daily"]}`);

            const mailData = {
                date: today,
                checkInTime,
                checkOutTime,
                additionalStaffNeeded: (tempCriticalStaff.length + tempBeforeCheckInStaff.length + tempGeneralStaff.length),
                criticalWindowStaffNeeded: tempCriticalStaff.length,
                additionalBeforeCheckInStaff: tempBeforeCheckInStaff.length,
                additionalGeneralStaff: tempGeneralStaff.length
            };

            if (additionalStaffNeeded) {
                await sendEmails(client, propertyId, mailData);
            }
        }
    } finally {
        if (client) {
            client.release();
        }
    }
};