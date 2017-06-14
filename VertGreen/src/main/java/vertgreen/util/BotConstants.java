/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package vertgreen.util;

import java.awt.*;

public class BotConstants {
    GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();
    public static final String MUSIC_BOT_ID = "184405311681986560";
    public static final String BETA_BOT_ID = "152691313123393536";
    public static final String MAIN_BOT_ID = "150376112944447488";
    public static final String PATRON_BOT_ID = "241950106125860865";

    public static final String FREDBOAT_HANGOUT_ID = "174820236481134592";
    public static final Color VERTGREEN_COLOR = new Color(2, 224, 9);
    public static final Color VERTYELLOW_COLOR = new Color(255, 246, 5);
    public static final Color VERTRED_COLOR = new Color(255, 5, 5);
    public static final String BOT_VERSION = "1.8" + gitRepoState.describe;
    public static final String BOT_RELEASE = "RELEASE";
    private BotConstants() {
    }

}
