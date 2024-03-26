package nl.tue.stratagrids;

import androidx.lifecycle.ViewModel;

public class OfflineGame extends ViewModel {

    private BaseGame game;

    public OfflineGame() {
        game = new BaseGame(5, 2);
    }

    public BaseGame getGame() {
        return game;
    }
}
