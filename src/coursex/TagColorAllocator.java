package coursex;

import java.util.*;

public class TagColorAllocator {
    public TagColorAllocator(Iterable<String> colors) {
        this._rand   = new Random();
        this._tags   = new HashMap<>();
        this._colors = new ArrayList<>();
        this._colorAvailable = new HashMap<>();
        for (var color : colors)
        {
            this._colors.add(color);
            this._colorAvailable.put(color, true);
        }
        this._colorAllocatedCount = 0;
    }

    public String getColor(Object tag) {
        if (this._tags.containsKey(tag))
            return this._tags.get(tag);
        int index;
        do  index = this._rand.nextInt(this._colors.size());
        while (! this._colorAvailable.get(this._colors.get(index)));
        var result = this._colors.get(index);
        this._tags.put(tag, result);
        this._colorAvailable.replace(result, false);
        if (++ this._colorAllocatedCount == this._colors.size()) {
            for (var color : this._colors)
                this._colorAvailable.replace(color, true);
            this._colorAllocatedCount = 0;
        }
        return result;
    }

    private Random               _rand;
    private Map<Object, String>  _tags;
    private List<String>         _colors;
    private Map<String, Boolean> _colorAvailable;
    private Integer              _colorAllocatedCount;

    public static final Iterable<String> MATERIAL_DESIGN = Arrays.asList(
        "#ffcdd2", "#f8bbd0", "#e1bee7", "#c5cae9", "#b3e5fc", "#b2ebf2",
        "#b2dfdb", "#c8e6c9", "#dcedc8", "#fff9c4", "#ffe0b2", "#ffccbc"
    );
}
