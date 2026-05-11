package hust.adventure.events;

import com.badlogic.gdx.utils.Pool;

public class RewardSelectedEvent implements Pool.Poolable {
    private String rewardId;

    public RewardSelectedEvent() {
    }

    public void init(String rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardId() {
        return rewardId;
    }

    @Override
    public void reset() {
        this.rewardId = null;
    }
}
