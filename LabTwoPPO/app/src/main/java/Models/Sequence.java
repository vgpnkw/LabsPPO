package Models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sequence {

    public Sequence(String name, int preparation, int work, int rest, int cycles, int sets,
                    int restBetweenSets, int calm, int colour) {
        this.name = name;
        this.preparation = preparation;
        this.work = work;
        this.rest = rest;
        this.cycles = cycles;
        this.sets = sets;
        this.restBetweenSets = restBetweenSets;
        this.calm = calm;
        this.colour = colour;
    }

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public int preparation;
    public int work;
    public int rest;
    public int cycles;
    public int sets;
    public int restBetweenSets;
    public int calm;
    public int colour;

}
