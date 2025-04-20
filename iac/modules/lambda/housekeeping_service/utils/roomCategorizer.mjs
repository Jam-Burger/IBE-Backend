// x
export function getRoomsWithSameCheckInAndOut(checkInSet, checkOutSet) {
    return checkInSet.intersection(checkOutSet);
}

// y
export function getRoomsCheckedOutOnly(checkOutSet, checkInSet) {
    return checkOutSet.difference(checkInSet);
}

// z
export function getRoomsCheckedInOnly(checkInSet, checkOutSet) {
    return checkInSet.difference(checkOutSet);
}

// w
export function getRoomsWithNoCheckInOrOut(bookedSet, checkInSet, checkOutSet) {
    return bookedSet.difference(checkInSet).difference(checkOutSet);
}