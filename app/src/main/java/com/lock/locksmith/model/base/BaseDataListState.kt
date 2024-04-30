package com.lock.locksmith.model.base

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
data class BaseDataListState(
    public val dataList: List<BaseData> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false) {
}