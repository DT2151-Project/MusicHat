package furhatos.app.musichatskill.flow

import furhatos.app.musichatskill.flow.main.Idle
import furhatos.app.musichatskill.setting.distanceToEngage
import furhatos.app.musichatskill.setting.maxNumberOfUsers
import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.voice.Voice

val Init : State = state() {
    init {
        /** Set our default interaction parameters */
        users.setSimpleEngagementPolicy(distanceToEngage, maxNumberOfUsers)
        furhat.voice = Voice("Matthew")
        /** start the interaction */
        goto(Idle)
    }
}
