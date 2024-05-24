package com.lock.locksmith.adapter.playload

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
data class ItemListPayloadDiff(
    val nameChanged: Boolean,
    val accountNameChanged: Boolean,
    val updateDateChanged: Boolean
    ) {

    public fun hasDifference(): Boolean =
        nameChanged
            .or(accountNameChanged)
            .or(updateDateChanged)

    public operator fun plus(other: ItemListPayloadDiff): ItemListPayloadDiff =
        copy(
            nameChanged = nameChanged || other.nameChanged,
            accountNameChanged = accountNameChanged || other.accountNameChanged,
            updateDateChanged = updateDateChanged || other.updateDateChanged
        )
}
