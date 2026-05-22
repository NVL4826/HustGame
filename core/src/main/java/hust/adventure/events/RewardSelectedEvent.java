package hust.adventure.events;
 
public class RewardSelectedEvent {
    private final String rewardId;
 
    public RewardSelectedEvent(String rewardId) {
        if (rewardId == null) throw new NullPointerException("rewardId cannot be null");
        this.rewardId = rewardId;
    }
 
    public String getRewardId() {
        return rewardId;
    }
}
