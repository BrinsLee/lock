/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lock.locksmith.adapter.viewholder.factory

import com.lock.locksmith.views.ItemListView

public sealed interface ItemListListenerContainer {
    public val itemClickListener: ItemListView.ItemClickListener
    public val itemLongClickListener: ItemListView.ItemLongClickListener
    public val deleteClickListener: ItemListView.ItemClickListener
    public val copyClickListener: ItemListView.ItemClickListener
    public val swipeListener: ItemListView.SwipeListener
}
