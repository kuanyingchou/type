package kuanying.type;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private TimeLabel timeLabel;
    private Button restartButton;
    private static final String punctuations = ",.;:";
    private static final String article = "MOUNTAIN VIEW, Calif. — LAST June, in an interview with Adam Bryant of The Times, Laszlo Bock, the senior vice president of people operations for Google — i.e., the guy in charge of hiring for one of the world’s most successful companies — noted that Google had determined that “G.P.A.’s are worthless as a criteria for hiring, and test scores are worthless. ... We found that they don’t predict anything.” He also noted that the “proportion of people without any college education at Google has increased over time” — now as high as 14 percent on some teams. At a time when many people are asking, “How’s my kid gonna get a job?” I thought it would be useful to visit Google and hear how Bock would answer.";


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

        final TextArea a = new TextArea(article, skin);
        final TextArea b = new TextArea("", skin);
        b.addListener(new InputListener() {
            public boolean keyTyped(InputEvent e, char c) {
                if(!timeLabel.isStarted() && c!=0 /* prevent Shift */) { 
                    timeLabel.start(); 
                }
                System.err.println(Main.wc(b.getText()));
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

    private void test_wc() {
        assertTrue(wc("") == 0);
        assertTrue(wc("hello") == 1);
        assertTrue(wc(" hello") == 1);
        assertTrue(wc("hello ") == 1);
        assertTrue(wc("hello, world!") == 2);
        assertTrue(wc("hello,world!") == 2);
        assertTrue(wc(article) == 126);
    }

    @Override
    public void render() {
        clearBackground(Color.GRAY);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
    private void clearBackground(Color c) {
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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
    
    public static void assertTrue(boolean exp) {
        if(!exp) throw new RuntimeException("assertion failed!");
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
