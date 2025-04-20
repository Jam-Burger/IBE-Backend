import {timeStrToMinutes} from "../utils/timeConverter.mjs";

export async function getAvailableStaffPerShift(client, propertyId) {
    const query = `
        SELECT sh.id                                                  AS shift_id,
               sh.shift_name,
               sh.start_time                                          AS shift_start,
               sh.end_time                                            AS shift_end,
               sh.property_id,
               COUNT(sa.staff_id)                                     AS available_staff_count,
               EXTRACT(EPOCH FROM (sh.end_time - sh.start_time)) / 60 AS shift_duration_minutes
        FROM shift sh
                 JOIN staff st ON st.preferred_shift_id = sh.id
                 JOIN staff_availability sa ON sa.staff_id = st.id
        WHERE sa.date = CURRENT_DATE
          AND sa.is_available = true
          AND st.is_permanent_staff = true
          AND sh.property_id = $1
        GROUP BY sh.id, sh.shift_name, sh.start_time, sh.end_time;
    `;

    const res = await client.query(query, [propertyId]);
    return res.rows;
}

export async function getShiftsByProperty(client, propertyId) {
    const query = `
        SELECT *
        FROM shift
        WHERE property_id = $1
    `;
    const {rows} = await client.query(query, [propertyId]);
    return rows;
}

export async function getStaffByShiftIds(client, shiftIds) {
    const query = `
        SELECT *
        FROM staff st
                 JOIN staff_availability sa ON sa.staff_id = st.id
        WHERE st.preferred_shift_id = ANY ($1::bigint[])
          AND st.is_permanent_staff = true
          AND sa.date = CURRENT_DATE
          AND sa.is_available = true
    `;
    const {rows} = await client.query(query, [shiftIds]);
    return rows;
}

export async function getAvailableStaffForDate(client, propertyId, date) {
    const query = `
        SELECT *
        FROM staff_availability
                 JOIN staff ON staff_availability.staff_id = staff.id
                 JOIN shift AS sh ON staff.preferred_shift_id = sh.id
        WHERE sh.property_id = $1
          AND date = $2
          AND is_available = TRUE
    `;
    const {rows} = await client.query(query, [propertyId, date]);
    return rows;
}

export async function getTemporaryStaff(client, count) {
    const result = await client.query(
        `SELECT *
         FROM staff
         WHERE is_permanent_staff = false
         ORDER BY RANDOM()
             LIMIT $1;`
        , [count]);
    return result.rows;
}

export async function getTemporaryStaffForShifts(client, shiftIds, count) {
    const query = `
        SELECT *
        FROM staff
        WHERE is_permanent_staff = false
          AND preferred_shift_id = ANY ($1::bigint[])
        ORDER BY RANDOM()
            LIMIT $2;
    `;
    const {rows} = await client.query(query, [shiftIds, count]);
    return rows;
}

export async function getCheckinCheckoutTime(client, propertyId) {
    const query = `
        SELECT check_in_time, check_out_time
        FROM property_extension
        WHERE id = $1
    `;
    const {rows} = await client.query(query, [propertyId]);
    return {
        checkInTime: rows[0].check_in_time,
        checkOutTime: rows[0].check_out_time
    };
}

export function getShiftsBeforeCheckIn(shifts, checkInMinutes) {
    return shifts.filter(shift => timeStrToMinutes(shift.end_time) <= checkInMinutes);
}
