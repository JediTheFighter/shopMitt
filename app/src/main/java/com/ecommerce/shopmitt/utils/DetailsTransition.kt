

import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet

public class DetailsTransition : TransitionSet() {

    fun DetailsTransition() {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds()).addTransition(ChangeTransform()).addTransition(
            ChangeImageTransform()
        )
    }

}