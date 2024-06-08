package uk.ac.bournemouth.ap.dotsandboxeslib.players

import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame

/**
 * Baseclass
 */
abstract class ComputerPlayerCommon(
    override val fullname: String,
    override val initials: String
) : ComputerPlayer, DrawablePlayer {

    protected fun DotsAndBoxesGame.findRandomPlayableEdge(): DotsAndBoxesGame.Line {
        val candidates = playableEdges().toList()
        return candidates.random()
    }

    protected fun DotsAndBoxesGame.playableEdges(): Sequence<DotsAndBoxesGame.Line> {
        return lines.asSequence()
            .filter { edge -> !edge.isDrawn }
    }

    override fun toString() =
        "${javaClass.simpleName.substringAfterLast('.')}( $fullname - $initials )"

}
