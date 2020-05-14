/*
 * Copyright (c) 2020 New Vector Ltd
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

package im.vector.matrix.android.internal.session.widgets

import im.vector.matrix.android.R
import im.vector.matrix.android.api.session.widgets.WidgetURLBuilder
import im.vector.matrix.android.internal.session.SessionScope
import im.vector.matrix.android.internal.session.integrationmanager.IntegrationManager
import im.vector.matrix.android.internal.session.integrationmanager.IntegrationManagerConfig
import im.vector.matrix.android.internal.session.widgets.token.GetScalarTokenTask
import im.vector.matrix.android.internal.util.StringProvider
import java.net.URLEncoder
import javax.inject.Inject

@SessionScope
internal class DefaultWidgetURLBuilder @Inject constructor(private val integrationManager: IntegrationManager,
                                                           private val getScalarTokenTask: GetScalarTokenTask,
                                                           private val stringProvider: StringProvider
) : IntegrationManager.Listener, WidgetURLBuilder {

    private var currentConfig: IntegrationManagerConfig? = null
    private var whiteListedUrls: List<String> = emptyList()

    fun start() {
        setupWithConfiguration()
        integrationManager.addListener(this)
    }

    fun stop() {
        integrationManager.removeListener(this)
    }

    override fun onConfigurationChanged(config: IntegrationManagerConfig) {
        setupWithConfiguration()
    }

    private fun setupWithConfiguration() {
        val preferredConfig = integrationManager.getPreferredConfig()
        if (currentConfig != preferredConfig) {
            currentConfig = preferredConfig
            val defaultWhiteList = stringProvider.getStringArray(R.array.integrations_widgets_urls).asList()
            whiteListedUrls = when (preferredConfig?.kind) {
                IntegrationManagerConfig.Kind.DEFAULT    -> defaultWhiteList
                IntegrationManagerConfig.Kind.ACCOUNT    -> defaultWhiteList + preferredConfig.apiUrl
                IntegrationManagerConfig.Kind.HOMESERVER -> listOf(preferredConfig.apiUrl)
                else                                     -> emptyList()
            }
        }
    }

    /**
     * Takes care of fetching a scalar token if required and build the final url.
     */
    override suspend fun build(baseUrl: String, params: Map<String, String>, forceFetchScalarToken: Boolean): String {
        return if (isScalarUrl(baseUrl) || forceFetchScalarToken) {
            val taskParams = GetScalarTokenTask.Params(baseUrl)
            val scalarToken = getScalarTokenTask.execute(taskParams)
            buildString {
                append(baseUrl)
                append("scalar_token", scalarToken)
                appendParamsToUrl(params)
            }
        } else {
            buildString {
                append(baseUrl)
                appendParamsToUrl(params)
            }
        }
    }

    private fun isScalarUrl(url: String): Boolean {
        val allowed: List<String> = whiteListedUrls
        for (allowedUrl in allowed) {
            if (url.startsWith(allowedUrl)) {
                return true
            }
        }
        return false
    }

    private fun StringBuilder.appendParamsToUrl(params: Map<String, String>): StringBuilder {
        params.forEach { (param, value) ->
            appendParamToUrl(param, value)
        }
        return this
    }

    private fun StringBuilder.appendParamToUrl(param: String, value: String): StringBuilder {
        if (contains("?")) {
            append("&")
        } else {
            append("?")
        }

        append(param)
        append("=")
        append(URLEncoder.encode(value, "utf-8"))

        return this
    }
}


