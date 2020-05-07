package coursex;

public class HomeworkSceneContext {
    public final TagColorAllocator courseColorAllocator;

    public HomeworkSceneContext() {
        this.courseColorAllocator = new TagColorAllocator(TagColorAllocator.MATERIAL_DESIGN);
    }
}
