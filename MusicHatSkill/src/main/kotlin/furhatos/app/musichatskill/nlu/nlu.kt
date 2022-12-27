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

class AskForPopMusic:Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Pop music",
            "I would Like pop music",
            "Pop",
            "Pop please"
        )
    }
}

class AskForRockMusic:Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Rock music",
            "I would Like rock music",
            "Rock",
            "Rock music please"
        )
    }
}

class AskForFolkMusic:Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Folk music",
            "I would Like folk music",
            "Folk",
            "Folk music please"
        )
    }
}

class AskForHipHopMusic:Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Hip Hop music",
            "I would Like hip hop music",
            "Hip hop",
            "Hip hop music please"
        )
    }
}


class AskForCountryMusic:Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Country music",
            "I would Like country music",
            "Country",
            "Country music music please"
        )
    }
}