export async function getTaskTypeDurations(client) {
    const result = await client.query(`
        SELECT id, type_name, required_time
        FROM clean_task_type
    `);

    const typeMap = {};
    for (const row of result.rows) {
        const normalized = row.type_name.toLowerCase();
        const duration = parseInt(row.required_time);
        if (normalized.includes("check-out")) {
            typeMap["checkout"] = duration;
        } else if (normalized.includes("check-in")) {
            typeMap["checkin"] = duration;
        } else if (normalized.includes("daily")) {
            typeMap["daily"] = duration;
        }
    }

    return typeMap;
}

export function calculateCleaningTime(roomCategories, taskTypeDurations) {
    const {
        sameDayRooms,
        checkoutOnlyRooms,
        checkinOnlyRooms,
        noActivityRooms,
    } = roomCategories;

    const beforeCheckInCleaningTime = sameDayRooms.size * taskTypeDurations.checkout +
        +checkinOnlyRooms.size * taskTypeDurations.checkin;

    const totalCleaningTime = beforeCheckInCleaningTime +
        checkoutOnlyRooms.size * taskTypeDurations.checkout +
        noActivityRooms.size * taskTypeDurations.daily;

    return {beforeCheckInCleaningTime, totalCleaningTime};
}