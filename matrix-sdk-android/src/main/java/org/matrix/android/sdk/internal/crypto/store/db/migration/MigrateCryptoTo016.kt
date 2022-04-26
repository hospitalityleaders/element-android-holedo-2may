/*
 * Copyright (c) 2022 The Matrix.org Foundation C.I.C.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoRoomEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.OlmInboundGroupSessionEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

// Version 16L enhance OlmInboundGroupSessionEntity to support shared history for MSC3061
internal class MigrateCryptoTo016(realm: DynamicRealm) : RealmMigrator(realm, 16) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("OlmInboundGroupSessionEntity")
                ?.addField(OlmInboundGroupSessionEntityFields.SHARED_HISTORY, Boolean::class.java)
                ?.addField(OlmInboundGroupSessionEntityFields.ROOM_ID, String::class.java)

        realm.schema.get("CryptoRoomEntity")
                ?.addField(CryptoRoomEntityFields.SHOULD_SHARE_HISTORY, Boolean::class.java)
    }
}
