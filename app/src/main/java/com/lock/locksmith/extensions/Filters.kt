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

package com.lock.locksmith.extensions

import com.lock.locksmith.model.filter.FilterObject
import com.lock.locksmith.model.filter.Filters

/**
 * Create the default channel list filter for the given user.
 *
 * @param user The currently logged in user.
 * @return The default filter for the channel list view.
 */
public fun Filters.defaultChannelListFilter(): FilterObject? {
    return and(
        eq("type", "messaging"),
    )
}
