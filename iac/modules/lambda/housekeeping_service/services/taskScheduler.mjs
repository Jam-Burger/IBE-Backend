import {minutesToTimeStr, timeStrToMinutes} from "../utils/timeConverter.mjs";

export function assignCleaningTasks({
                                        date,
                                        checkInMinutes,
                                        checkOutMinutes,
                                        sameDayRooms, checkoutOnlyRooms, checkinOnlyRooms, noActivityRooms,
                                        staff,
                                        shifts,
                                        availability,
                                        durations
                                    }) {
    const tasks = [
        ...[...sameDayRooms].map(id => ({
            room_id: id,
            type: "immediate-checkout",
            duration: durations.checkout,
            deadline: checkInMinutes,
            startAfter: checkOutMinutes // Add startAfter constraint for checkout rooms
        })),
        ...[...checkoutOnlyRooms].map(id => ({
            room_id: id,
            type: "checkout",
            duration: durations.checkout,
            deadline: null,
            startAfter: checkOutMinutes // Add startAfter constraint for checkout rooms
        })),
        ...[...checkinOnlyRooms].map(id => ({
            room_id: id,
            type: "checkin",
            duration: durations.checkin,
            deadline: checkInMinutes,
            startAfter: null // No start constraint for check-in rooms
        })),
        ...[...noActivityRooms].map(id => ({
            room_id: id,
            type: "daily",
            duration: durations.daily,
            deadline: null,
            startAfter: null // No start constraint for daily cleaning
        }))
    ];

    // Sort each group by deadline (prioritizing those with deadlines) and then by duration (longer tasks first)
    const sortByDeadlineAndDuration = (a, b) => {
        if (a.deadline !== b.deadline) {
            return (a.deadline ?? Infinity) - (b.deadline ?? Infinity);
        }
        return b.duration - a.duration;
    };

    const sortByMinimumWorkDone = (a, b) => {
        const aWorkDone = a.assignedTasks.reduce((acc, task) => acc + task.duration, 0);
        const bWorkDone = b.assignedTasks.reduce((acc, task) => acc + task.duration, 0);
        if (aWorkDone === bWorkDone) {
            return a.currentTime - b.currentTime;
        }
        return aWorkDone - bWorkDone;
    }

    tasks.sort(sortByDeadlineAndDuration);

    const staffMap = staff.reduce((acc, member) => {
        const isAvailable = availability.find(a => a.staff_id === member.id && a.is_available);
        if (!isAvailable) return acc;

        const shift = shifts.find(s => s.id === member.preferred_shift_id);
        if (!shift) return acc;

        acc[member.id] = {
            ...member,
            shiftStart: timeStrToMinutes(shift.start_time),
            shiftEnd: timeStrToMinutes(shift.end_time),
            currentTime: timeStrToMinutes(shift.start_time),
            assignedTasks: []
        };
        return acc;
    }, {});

    const scheduledTasks = [];
    let unassignedTasks = [];

    const staffMembersShuffled = Object.values(staffMap);

    for (const task of tasks) {
        let assigned = false;

        for (let member of staffMembersShuffled) {
            // Simple check - can we fit this task in the current time slot?
            const availableTime = member.shiftEnd - member.currentTime;
            if (availableTime < task.duration) continue;

            // Check deadline constraint
            const taskEnd = member.currentTime + task.duration;
            if (task.deadline && taskEnd > task.deadline) continue;

            scheduledTasks.push({
                staff_id: member.id,
                staff_name: member.staff_name,
                room_id: task.room_id,
                task_type: task.type,
                start_time: minutesToTimeStr(member.currentTime),
                end_time: minutesToTimeStr(member.currentTime + task.duration),
                date
            });

            member.assignedTasks.push(task);
            member.currentTime += task.duration;
            assigned = true;
            break;
        }
        staffMembersShuffled.sort(sortByMinimumWorkDone);

        if (!assigned) {
            unassignedTasks.push(task);
        }
    }

    // Try to optimize further - if there are unassigned tasks without startAfter,
    // see if we can fit them in any gaps created by startAfter constraints
    if (unassignedTasks.length > 0) {
        const remainingTasksWithoutStartAfter = unassignedTasks.filter(task => task.startAfter === null);
        unassignedTasks = unassignedTasks.filter(task => task.startAfter !== null);

        for (const task of remainingTasksWithoutStartAfter) {
            for (let member of staffMembersShuffled) {
                // Find upcoming time constraints from already assigned tasks
                const futureConstraints = member.assignedTasks
                    .filter(t => t.startAfter && t.startAfter > member.currentTime)
                    .map(t => t.startAfter)
                    .sort((a, b) => a - b);

                const nextConstraint = futureConstraints.length > 0 ? futureConstraints[0] : member.shiftEnd;

                // Check if we can fit this task before the next constraint
                const availableGap = nextConstraint - member.currentTime;
                if (availableGap >= task.duration) {
                    // Check deadline constraint
                    const taskEnd = member.currentTime + task.duration;
                    if (task.deadline && taskEnd > task.deadline) continue;

                    scheduledTasks.push({
                        staff_id: member.id,
                        staff_name: member.staff_name,
                        room_id: task.room_id,
                        task_type: task.type,
                        start_time: minutesToTimeStr(member.currentTime),
                        end_time: minutesToTimeStr(member.currentTime + task.duration),
                        date
                    });

                    member.assignedTasks.push(task);
                    member.currentTime += task.duration;
                    break;
                }
            }
        }
    }

    if (unassignedTasks.length > 0) {
        console.warn("Some tasks were not scheduled. Check task assignment logic.");
        console.info("Unassigned tasks:", unassignedTasks);
        staffMembersShuffled.forEach((member) => {
            console.info("member:", member);
        });
        throw new Error("Task assignment failed");
    }

    return scheduledTasks;
}

async function getTaskTypeMap(client) {
    const result = await client.query(`
        SELECT id, type_name
        FROM clean_task_type
    `);

    const mapping = {};
    for (const row of result.rows) {
        const normalized = row.type_name.toLowerCase();
        if (normalized.includes("check-out")) {
            mapping["checkout"] = row.id;
            mapping["immediate-checkout"] = row.id;
        } else if (normalized.includes("check-in")) {
            mapping["checkin"] = row.id;
        } else if (normalized.includes("daily")) {
            mapping["daily"] = row.id;
        }
    }

    return mapping;
}

export const deleteReport = async (client, date) => {
    await client.query(`
        DELETE
        FROM clean_task
        WHERE date = $1`, [date]);
}

export const insertTasks = async (client, tasks) => {
    const taskTypeMap = await getTaskTypeMap(client);
    console.log("inserting tasks...");
    for (const task of tasks) {
        const taskTypeId = taskTypeMap[task.task_type];
        if (!taskTypeId) continue;

        const insertQuery = `
            INSERT INTO clean_task (staff_id, start_time, task_type_id, date, room_id)
            VALUES ($1, $2, $3, $4, $5)
        `;

        await client.query(insertQuery, [
            task.staff_id,
            task.start_time,
            taskTypeId,
            task.date,
            task.room_id
        ]);
    }
    console.log("Task insertion complete");
};