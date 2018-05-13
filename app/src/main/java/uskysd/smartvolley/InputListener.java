package uskysd.smartvolley;

/**
 * Created by yusukeyohishida on 2/28/18.
 */

public class InputListener {

    public InputListener() {}
    public void onPlayerTouched(int playerId, int x, int y) {
        // Add play data
    }
    public void onPlayerLongTouched(int playerId, int x, int y) {

    }

    public void onScoreBoardRightTouched(int x, int y) {
        //Add point to the team on the right court
    }

    public void onScoreBoardLeftTouched(int x, int y) {
        // Add point to the team on the left court
    }

    public void onRotationBoardLeftTouched(int x, int y) {
        // Rotation left team
    }

    public void onRotationBoardRightTouched(int x, int y) {
        // Rotation right team
    }

    public void onPlayerSwiped(int playerId, int startX, int startY, int endX, int endY) {

    }

    public void onPlayerSwipedToBench(int playerId) {
        //Member change request
    }

    public void onLeftSideBenchLongTouched(int x, int y) {

    }

    public void onRightSideBenchLongTouched(int x, int y) {

    }

    public void onLeftCourtInsideTouched(int x, int y) {

    }

    public void onLeftCourtOutsideTouched(int x, int y) {

    }

    public void onRightCourtInsideTouched(int x, int y) {

    }

    public void onRightCourtOutsideTouched(int x, int y) {

    }

    public void onLeftSideBackLeftSwipedToSeviceArea(int playerId, int x, int y) {

    }

    public void onRightSideBackLeftSwipeToServiceArea(int playerId, int x, int y) {

    }

    public void onScoreBoardLongTouched(int x, int y) {

    }

    public void onUndoButtonClicked() {

    }

    public void onReduButtonClicked() {

    }
}
