package uk.ac.bournemouth.ap.dotsandboxeslib.players

import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer

class DrawableHumanPlayer(
    override val fullname: String,
    override val initials: String=fullname.substring(0,1)
): HumanPlayer, DrawablePlayer {
    override fun toString() = "DrawableHumanPlayer( $fullname - $initials )"
}