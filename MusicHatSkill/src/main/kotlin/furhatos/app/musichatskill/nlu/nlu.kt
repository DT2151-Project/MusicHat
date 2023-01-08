package furhatos.app.musichatskill.nlu

import furhatos.nlu.Intent
import furhatos.util.Language

class DMConfirm : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Dance maniac",
            "I'm a dance maniac",
            "I love dancing",
            "Dancer"
        )
    }
}

class CLConfirm : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Casual listener",
            "I'm a casual listener",
            "Casual",
            "Just a casual listener"
        )
    }
}

class MNConfirm : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Music nerd",
            "I'm a music nerd",
            "I'm a nerd",
            "Nerd"
        )
    }
}

class AskRecommendations: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("What do you recommend?",
            "Recommend",
            "Recommend something",
            "You decide")
    }
}

class IDK: Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("I don't know.",
            "Whatever",
            "I forgot.",
            "Anyone is ok.")
    }
}


class YesStaff: Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("Great.",
                "Yeah",
                "Yea",
                "That is great",
                "Awesome")
    }
}
