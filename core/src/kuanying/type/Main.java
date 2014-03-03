package kuanying.type;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

public class Main extends ApplicationAdapter {
    @Override
    public void create () {
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        timeLabel = new TimeLabel(skin);

        restartButton = new TextButton("Restart", skin);
        restartButton.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                System.out.println("click " + x + ", " + y);
            }
        });

        final HorizontalGroup a = new HorizontalGroup();
        for(int i=0; i<article.length(); i++) {
            final Label c = 
                    new CharLabel(String.valueOf(article.charAt(i)), skin);
            //c.setColor(Color.RED);
            a.addActor(c);
            
        }
        final SnapshotArray<Actor> chars = a.getChildren();

        final TextArea b = new TextArea("", skin);
        b.addListener(new InputListener() {
            public boolean keyTyped(InputEvent e, char c) {
                if(!timeLabel.isStarted() && c!=0 /* prevent Shift */) { 
                    timeLabel.start(); 
                    final CharLabel nLabel = 
                            (CharLabel)chars.get(index);
                    nLabel.setActive(true);
                }
                //System.err.println(Main.wc(b.getText()));
                //>>> the only way to move back to 'backspace'
                if(e.getKeyCode() == Input.Keys.BACKSPACE) {
                    if(index >0) {
                        index--;
                        final CharLabel nLabel = 
                                (CharLabel)chars.get(index);
                        nLabel.setActive(true);
                        //final Label cLabel = (Label)chars.get(index);
                        //cLabel.setColor(Color.WHITE);
                    }
                } else {
                    final CharLabel cLabel = (CharLabel)chars.get(index);
                    if(isValid(c)) {
                        /*
                        if(c == article.charAt(index)) {
                            cLabel.setColor(Color.GREEN);
                        } else {
                            cLabel.setColor(Color.RED);
                        }
                        */
                        cLabel.setActive(false);
                        index++;
                        final CharLabel nLabel = 
                                (CharLabel)chars.get(index);
                        nLabel.setActive(true);

                    }
                }
                return false;
            } 
        });

        final SplitPane pane = new SplitPane(a, b, true, skin);
        //pane.setFillParent(true);


        final Table table = new Table();
        table.setFillParent(true);
        table.row();
        table.add(restartButton).left().expandX();
        table.add(timeLabel).right().expandX();
        table.row();
        table.add(pane).expand().fill().colspan(2);
        table.row().space(10);

        stage = new Stage();
        stage.setKeyboardFocus(b);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

        //[unit tests >>>
        test_wc();
        //]
    }

    private boolean isValid(char c) {
        if(Character.isAlphabetic(c) || Character.isWhitespace(c) ||
                punctuations.contains(String.valueOf(c))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void render() {
        clearBackground(Color.GRAY);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
    @Override 
    public void resize(int w, int h) {
        stage.setViewport(w, h, false);
    }

    @Override 
    public void dispose() {
        stage.dispose();
    }

    public static int wc(String input) {
        int count = 0;
        boolean inWord = false;
        for(int i=0; i<input.length(); i++) {
            final char c = input.charAt(i);
            if(inWord) {
                if(Character.isWhitespace(c) || 
                        punctuations.contains(String.valueOf(c))) {
                    inWord = false;
                }
            } else {
                if(Character.isAlphabetic(c)) {
                    inWord = true;
                    count ++;
                }
            }
        }
        return count;
    }
    private void clearBackground(Color c) {
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void test_wc() {
        assertTrue(wc("") == 0);
        assertTrue(wc("hello") == 1);
        assertTrue(wc(" hello") == 1);
        assertTrue(wc("hello ") == 1);
        assertTrue(wc("hello, world!") == 2);
        assertTrue(wc("hello,world!") == 2);
        assertTrue(wc(article) == 126);
    }

    
    public static void assertTrue(boolean exp) {
        if(!exp) throw new RuntimeException("assertion failed!");
    }

    private Stage stage;
    private Skin skin;
    private TimeLabel timeLabel;
    private Button restartButton;
    private static final String punctuations = ",.;:";
    private static final String article = "MOUNTAIN VIEW, Calif. — LAST June, in an interview with Adam Bryant of The Times, Laszlo Bock, the senior vice president of people operations for Google — i.e., the guy in charge of hiring for one of the world’s most successful companies — noted that Google had determined that “G.P.A.’s are worthless as a criteria for hiring, and test scores are worthless. ... We found that they don’t predict anything.” He also noted that the “proportion of people without any college education at Google has increased over time” — now as high as 14 percent on some teams. At a time when many people are asking, “How’s my kid gonna get a job?” I thought it would be useful to visit Google and hear how Bock would answer.";
    private int index = 0;



}

class CharLabel extends Label {
    private boolean isActive = false;
    private static final Color[] blackColors = new Color[32];
    static {
        for(int i=0; i<blackColors.length; i++) {
            final float v = (float)i / blackColors.length;
            blackColors[i] = new Color(v, 1-v, 0, 1);
        }
    }
    private float elapsed = 0;

    public CharLabel(String text, Skin skin) {
        super(text, skin);
    }

    public void setActive(boolean b) { 
        isActive = b; 
        //if(isActive) elapsed = 0;
    }
    public boolean isActive() { return isActive; }

    @Override 
    public void act(float delta) {
        super.act(delta);
        if(!isActive) return;
        elapsed += delta;
        final float ratio = Math.min(elapsed / .5f, 1);
        final int i = Math.round((blackColors.length-1) * ratio);
        setColor(blackColors[i]);
    }
}

class TimeLabel extends Label {
    private boolean started = false;
    private float elapsed = 0;

    public TimeLabel(Skin skin) {
        super("Time: ", skin);
    }

    public boolean isStarted() {
        return started;
    }

    public void start() {
        started = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(started) {
            elapsed += delta;
            setText("Time Elapsed: "+ String.format("%.2fs", elapsed));
        }
    }
}
