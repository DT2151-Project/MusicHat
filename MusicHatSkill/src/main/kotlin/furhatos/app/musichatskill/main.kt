package furhatos.app.musichatskill

import furhatos.app.musichatskill.flow.*
import furhatos.flow.kotlin.*
import furhatos.skills.Skill

class MusichatskillSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}