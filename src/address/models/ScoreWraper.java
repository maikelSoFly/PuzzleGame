package address.models;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//
//     Project name: PuzzleGame
//
//     Created by maikel on 14.04.2017.
//     Copyright © 2017 Mikołaj Stępniewski. All rights reserved.
//

@XmlRootElement(name = "scores")
public class ScoreWraper {

    private List<Score> scores;

    @XmlElement(name = "score")
    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}