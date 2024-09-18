package com.imcys.shiquzkarticledetaildemo.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailData(
    @SerialName("bgmUrl")
    val bgmUrl: String,
    @SerialName("contentList")
    val contentList: List<Content>,
    @SerialName("cover")
    val cover: String,
    @SerialName("id")
    val id: Int,
    @SerialName("imgList")
    val imgList: List<String>,
    @SerialName("level")
    val level: Int,
    @SerialName("questionList")
    val questionList: List<Question>,
    @SerialName("readCount")
    val readCount: Int,
    @SerialName("readId")
    val readId: Int,
    @SerialName("readReportId")
    val readReportId: Int,
    @SerialName("talkItAudio")
    val talkItAudio: String,
    @SerialName("talkItContent")
    val talkItContent: String,
    @SerialName("title")
    val title: String,
    @SerialName("typeId")
    val typeId: Int,
    @SerialName("typeName")
    val typeName: String,
    @SerialName("wordNum")
    val wordNum: Int
) {
    @Serializable
    data class Content(
        @SerialName("audioDuration")
        val audioDuration: Int,
        @SerialName("audioUrl")
        val audioUrl: String,
        @SerialName("frameType")
        val frameType: Int,
        @SerialName("imgUrl")
        val imgUrl: String,
        @SerialName("pageNum")
        val pageNum: Int,
        @SerialName("sentence")
        val sentence: String,
        @SerialName("sentenceByXFList")
        val sentenceByXFList: List<SentenceByXF>
    ) {
        @Serializable
        data class SentenceByXF(
            @SerialName("wb")
            val wb: Int,
            @SerialName("we")
            val we: Int,
            @SerialName("word")
            val word: String
        )
    }

    @Serializable
    data class Question(
        @SerialName("answerList")
        val answerList: List<Answer>,
        @SerialName("correctAnswer")
        val correctAnswer: String,
        @SerialName("question")
        val question: String,
        @SerialName("questionAudio")
        val questionAudio: String,
        @SerialName("questionImg")
        val questionImg: String
    ) {
        @Serializable
        data class Answer(
            @SerialName("answer")
            val answer: String,
            @SerialName("audio")
            val audio: String
        )
    }
}