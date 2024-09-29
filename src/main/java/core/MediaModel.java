package core;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class MediaModel {
    @Setter
    public boolean isUploaded;
    private final String id;
    private final String path;

    public MediaModel(String id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public String toString() {
        return "MediaModel[" +
                "id=" + id + ", " +
                "path=" + path + ']';
    }
}
