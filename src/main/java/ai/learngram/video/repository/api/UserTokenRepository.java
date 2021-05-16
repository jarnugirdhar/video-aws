package ai.learngram.video.repository.api;

public interface UserTokenRepository {

    public String store(String id);

    public String fetchByToken(String token);

    public boolean invalidate(String token);

}
