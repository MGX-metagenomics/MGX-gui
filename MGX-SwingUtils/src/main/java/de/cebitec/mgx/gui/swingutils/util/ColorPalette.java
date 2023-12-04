/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author
 * https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
 */
public class ColorPalette {

    // https://lospec.com/palette-list/colors-basic
    private final static Color[] PALETTE_BASIC = new Color[]{
        //Color.decode("#f9fae8"),
        //Color.decode("#f3e2ae"),
        Color.decode("#edbb34"),
        Color.decode("#b65e28"),
        Color.decode("#7c3226"),
        Color.decode("#5b2137"),
        Color.decode("#061227"),
        Color.decode("#fab252"),
        Color.decode("#ed6b39"),
        Color.decode("#d52941"),
        Color.decode("#961650"),
        Color.decode("#5f0e4b"),
        Color.decode("#2f0a35"),
        Color.decode("#f2ffb8"),
        Color.decode("#b9f779"),
        Color.decode("#76e24f"),
        Color.decode("#1ab349"),
        Color.decode("#238b77"),
        Color.decode("#154b5c"),
        Color.decode("#8fe5ea"),
        Color.decode("#6bc3e2"),
        Color.decode("#448ad5"),
        Color.decode("#2140a3"),
        Color.decode("#26167e")
    };

    //
    // https://lospec.com/palette-list/blk-nx64
    // but white and black removed
    private final static Color[] PALETTE = new Color[]{
        Color.decode("#12173d"),
        Color.decode("#293268"),
        Color.decode("#464b8c"),
        Color.decode("#6b74b2"),
        Color.decode("#909edd"),
        Color.decode("#c1d9f2"),
        Color.decode("#a293c4"),
        Color.decode("#7b6aa5"),
        Color.decode("#53427f"),
        Color.decode("#3c2c68"),
        Color.decode("#431e66"),
        Color.decode("#5d2f8c"),
        Color.decode("#854cbf"),
        Color.decode("#b483ef"),
        Color.decode("#8cff9b"),
        Color.decode("#42bc7f"),
        Color.decode("#22896e"),
        Color.decode("#14665b"),
        Color.decode("#0f4a4c"),
        Color.decode("#0a2a33"),
        Color.decode("#1d1a59"),
        Color.decode("#322d89"),
        Color.decode("#354ab2"),
        Color.decode("#3e83d1"),
        Color.decode("#50b9eb"),
        Color.decode("#8cdaff"),
        Color.decode("#53a1ad"),
        Color.decode("#3b768f"),
        Color.decode("#21526b"),
        Color.decode("#163755"),
        Color.decode("#008782"),
        Color.decode("#00aaa5"),
        Color.decode("#27d3cb"),
        Color.decode("#78fae6"),
        Color.decode("#cdc599"),
        Color.decode("#988f64"),
        Color.decode("#5c5d41"),
        Color.decode("#353f23"),
        Color.decode("#919b45"),
        Color.decode("#afd370"),
        Color.decode("#ffe091"),
        Color.decode("#ffaa6e"),
        Color.decode("#ff695a"),
        Color.decode("#b23c40"),
        Color.decode("#ff6675"),
        Color.decode("#dd3745"),
        Color.decode("#a52639"),
        Color.decode("#721c2f"),
        Color.decode("#b22e69"),
        Color.decode("#e54286"),
        Color.decode("#ff6eaf"),
        Color.decode("#ffa5d5"),
        Color.decode("#ffd3ad"),
        Color.decode("#cc817a"),
        Color.decode("#895654"),
        Color.decode("#61393b"),
        Color.decode("#3f1f3c"),
        Color.decode("#723352"),
        Color.decode("#994c69"),
        Color.decode("#c37289"),
        Color.decode("#f29faa"),
        Color.decode("#ffccd0")
    };

    // https://stackoverflow.com/questions/2328339/how-to-generate-n-different-colors-for-any-natural-number-n
    
    private static final String[] indexcolors = new String[]{
        //"#000000", 
        "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
        "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87",
        "#5A0007", "#809693", "#FEFFE6", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80",
        "#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100",
        "#DDEFFF", "#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F",
        "#372101", "#FFB500", "#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
        "#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66",
        "#885578", "#FAD09F", "#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED", "#886F4C",
        "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375", "#A3C8C9", "#FF913F", "#938A81",
        "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757", "#C8A1A1", "#1E6E00",
        "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
        "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329",
        "#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72", "#6A3A4C",
        "#83AB58", "#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800",
        "#BF5650", "#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51",
        "#C895C5", "#320033", "#FF6832", "#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58"
    };

    public static List<Color> from64Palette(int num) {
        List<Color> colors = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            colors.add(Color.decode(indexcolors[i % indexcolors.length]));
        }
        return colors;
    }

    public static List<Color> pick(int num) {
        List<Color> colors = new ArrayList<>(num);
        float dx = 1.0f / (float) (num - 1);
        for (int i = 0; i < num; i++) {
            colors.add(get(1f * i * dx));
        }
        return colors;
    }

    public static Color get(float x) {
        float r = 0.0f;
        float g = 0.0f;
        float b = 1.0f;
        if (x >= 0.0f && x < 0.2f) {
            x = x / 0.2f;
            r = 0.0f;
            g = x;
            b = 1.0f;
        } else if (x >= 0.2f && x < 0.4f) {
            x = (x - 0.2f) / 0.2f;
            r = 0.0f;
            g = 1.0f;
            b = 1.0f - x;
        } else if (x >= 0.4f && x < 0.6f) {
            x = (x - 0.4f) / 0.2f;
            r = x;
            g = 1.0f;
            b = 0.0f;
        } else if (x >= 0.6f && x < 0.8f) {
            x = (x - 0.6f) / 0.2f;
            r = 1.0f;
            g = 1.0f - x;
            b = 0.0f;
        } else if (x >= 0.8f && x <= 1.0f) {
            x = (x - 0.8f) / 0.2f;
            r = 1.0f;
            g = 0.0f;
            b = x;
        }
        return new Color(r, g, b, 0.7f);
    }

    private ColorPalette() {
    }

}
