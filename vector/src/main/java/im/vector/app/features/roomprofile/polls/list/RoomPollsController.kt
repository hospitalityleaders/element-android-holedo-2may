/*
 * Copyright (c) 2022 New Vector Ltd
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

package im.vector.app.features.roomprofile.polls.list

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.app.R
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.roomprofile.polls.PollSummary
import javax.inject.Inject

class RoomPollsController @Inject constructor(
        val dateFormatter: VectorDateFormatter,
        val stringProvider: StringProvider,
) : TypedEpoxyController<List<PollSummary>>() {

    interface Listener {
        fun onPollClicked(pollId: String)
    }

    var listener: Listener? = null

    override fun buildModels(data: List<PollSummary>?) {
        if (data.isNullOrEmpty()) {
            return
        }

        for (poll in data) {
            when (poll) {
                is PollSummary.ActivePoll -> buildActivePollItem(poll)
                is PollSummary.EndedPoll -> buildEndedPollItem(poll)
            }
        }
    }

    private fun buildActivePollItem(poll: PollSummary.ActivePoll) {
        val host = this
        roomPollItem {
            id(poll.id)
            formattedDate(host.dateFormatter.format(poll.creationTimestamp, DateFormatKind.TIMELINE_DAY_DIVIDER))
            title(poll.title)
            clickListener {
                host.listener?.onPollClicked(poll.id)
            }
        }
    }

    private fun buildEndedPollItem(poll: PollSummary.EndedPoll) {
        val host = this
        roomPollItem {
            id(poll.id)
            formattedDate(host.dateFormatter.format(poll.creationTimestamp, DateFormatKind.TIMELINE_DAY_DIVIDER))
            title(poll.title)
            winnerOptions(poll.winnerOptions)
            totalVotesStatus(host.stringProvider.getQuantityString(R.plurals.poll_total_vote_count_after_ended, poll.totalVotes, poll.totalVotes))
            clickListener {
                host.listener?.onPollClicked(poll.id)
            }
        }
    }
}
