package com.fricht.mygame.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fricht.mygame.rendering.animations.Animation;
import com.fricht.mygame.rendering.animations.ChangePawn;
import com.fricht.mygame.rendering.animations.SetPawn;
import com.fricht.mygame.rendering.animations.Slide;

import java.util.ArrayList;


public class GameRenderer {

    public static float ANIMATION_DURATION = 0.4f;  // in sec

    private static final int[] players_num = {Board.P1, Board.P2};
    private static final float[][] players_bg = {{0f, 0f, 0.2f}, {0.4f, 0f, 0f}};
    private int current_player;
    private final boolean[] has_played;  // for the first move

    private final Sprite tile_sprite;
    private final Sprite[] blue_sprite;
    private final Sprite[] red_sprite;
    private int[][][] squares_pos;
    private float square_size;

    private boolean is_animating;
    private boolean wait_for_input;
    private final ArrayList<Animation> animations;
    private float animation_progress;

    public final int board_size;
    private Board board;
    private Board new_board;

    public GameRenderer(int board_size) {
        tile_sprite = new Sprite(new Texture("tile.png"));
        blue_sprite = new Sprite[5];
        red_sprite = new Sprite[5];
        for (int i = 0; i < 5; i++) {
            blue_sprite[i] = new Sprite(new Texture("blue-" + i + ".png"));
            red_sprite[i] = new Sprite(new Texture("red-" + i + ".png"));
        }
        current_player = 0;
        has_played = new boolean[]{false, false};
        animations = new ArrayList<>();
        is_animating = false;
        wait_for_input = true;
        animation_progress = 0f;
        this.board_size = board_size;
        board = new Board(board_size);
        new_board = new Board(board_size);
        regen_squares_pos();
        check_animations();
    }

    public int get_player_num() {
        return players_num[current_player];
    }

    public void next_player() {
        current_player = (current_player + 1) % 2;
    }

    private Sprite get_sprite(Sprite[] list, int index) {
        // prevent out of bound
        return list[Math.min(4, Math.max(0, index))];
    }

    public void regen_squares_pos() {
        squares_pos = new int[board_size][board_size][2];
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int restrictive_size = Math.min(width, height);
        square_size = restrictive_size * 0.9f / board_size;  // 0.9f gives margin (10%)
        float[] grid_origin = {width / 2f - square_size * board_size / 2, height / 2f - square_size * board_size / 2};
        for (int x = 0; x < board_size; x++) {
            for (int y = 0; y < board_size; y++) {
                squares_pos[y][x][0] = (int)(grid_origin[0] + square_size * x);
                squares_pos[y][x][1] = (int)(grid_origin[1] + square_size * y);
            }
        }
        tile_sprite.setSize(square_size, square_size);
        for (int i = 0; i < 5; i++) {
            blue_sprite[i].setSize(square_size, square_size);
            red_sprite[i].setSize(square_size, square_size);
        }
    }

    public void update(float delta_time) {
        if (is_animating) {
            if (animation_progress >= 1f) {
                stop_anim();
                not_anim_actions();
            } else {
                // forward animation
                animation_progress = Math.min(1f, animation_progress + delta_time / ANIMATION_DURATION);
            }
        } else {
            not_anim_actions();
        }
    }

    private void check_game_over() {
        boolean can_1_play = false;
        boolean can_2_play = false;
        for (int x = 0; x < board_size; x++) {
            for (int y = 0; y < board_size; y++) {
                if (board.can_play_at(x, y, Board.P1)) {
                    can_1_play = true;
                } else if (board.can_play_at(x, y, Board.P2)) {
                    can_2_play = true;
                }
                if (can_1_play && can_2_play) {
                    break;
                }
            }
        }
        if (!can_1_play) {
            // TODO player 2 won
        } else if (!can_2_play) {
            // TODO player 1 won
        }
    }

    private void not_anim_actions() {  // actions to do when not animating
        if (wait_for_input) {
            check_game_over();
            if (Gdx.input.justTouched()) {
                int sq_x = (int)((Gdx.input.getX() - squares_pos[0][0][0]) / square_size);
                // flip y axis because not same ref frame by default
                int sq_y = (int)((Gdx.input.getY() + 2 * (Gdx.graphics.getHeight() / 2f - Gdx.input.getY()) - squares_pos[0][0][1]) / square_size);
                if (!has_played[current_player]) {
                    int enemy_num = -get_player_num();
                    if (  // cannot place pawn if enemy near (if there is overlap during first split)
                        !(
                            // center
                            board.can_play_at(sq_x, sq_y, enemy_num) ||
                            // 8 neighbouring cells
                            board.can_play_at(sq_x + 1, sq_y + 1, enemy_num) ||
                            board.can_play_at(sq_x + 1, sq_y, enemy_num) ||
                            board.can_play_at(sq_x + 1, sq_y - 1, enemy_num) ||
                            board.can_play_at(sq_x, sq_y + 1, enemy_num) ||
                            board.can_play_at(sq_x, sq_y - 1, enemy_num) ||
                            board.can_play_at(sq_x - 1, sq_y + 1, enemy_num) ||
                            board.can_play_at(sq_x - 1, sq_y, enemy_num) ||
                            board.can_play_at(sq_x - 1, sq_y - 1, enemy_num) ||
                            // 4 extreme direct cells
                            board.can_play_at(sq_x + 2, sq_y, enemy_num) ||
                            board.can_play_at(sq_x - 2, sq_y, enemy_num) ||
                            board.can_play_at(sq_x, sq_y + 2, enemy_num) ||
                            board.can_play_at(sq_x, sq_y - 2, enemy_num) ||
                            // still on the board
                            board.is_out_of_bound(sq_x, sq_y)
                        )
                    ) {
                        board.add(sq_x, sq_y, 3, get_player_num());
                        has_played[current_player] = true;
                        next_player();
                    }
                } else {
                    if (board.can_play_at(sq_x, sq_y, get_player_num())) {
                        board.add(sq_x, sq_y, 1, get_player_num());
                        next_player();
                    }
                }
                check_animations();
            } // else do nothing, just wait for input
        } else {
            check_animations();
        }
    }

    private void check_animations() {
        animations.clear();
        ArrayList<int[]> split_cells = new ArrayList<>();  // x, y, player
        for (int x = 0; x < board_size; x++) {
            for (int y = 0; y < board_size; y++) {
                int[][] current_cell = board.get_at(x, y);
                if (current_cell[2][1] != 0) {
                    int[][] new_cell = board.get_merged_at(x, y);
                    new_board.set_at(x, y, new_cell);
                    if (current_cell[0][1] == 0 && current_cell[1][1] == 0) {
                        animations.add(new SetPawn(new Vector2(squares_pos[y][x][0], squares_pos[y][x][1]), get_sprite(current_cell[2][1] == 1 ? blue_sprite : red_sprite, current_cell[2][0])));
                    } else {
                        animations.add(new ChangePawn(new Vector2(squares_pos[y][x][0], squares_pos[y][x][1]), get_sprite(current_cell[2][1] == 1 ? blue_sprite : red_sprite, current_cell[2][0]), get_sprite(new_cell[0][1] == 1 ? blue_sprite : red_sprite, new_cell[0][0])));
                    }
                } else if (current_cell[0][0] >= 4) {
                    Vector2 from = new Vector2(squares_pos[y][x][0], squares_pos[y][x][1]);
                    if (y > 0) {
                        animations.add(new Slide(from, new Vector2(squares_pos[y - 1][x][0], squares_pos[y - 1][x][1]), get_sprite(current_cell[0][1] == 1 ? blue_sprite : red_sprite, 1)));
                    }
                    if (y < board_size - 1) {
                        animations.add(new Slide(from, new Vector2(squares_pos[y + 1][x][0], squares_pos[y + 1][x][1]), get_sprite(current_cell[0][1] == 1 ? blue_sprite : red_sprite, 1)));
                    }
                    if (x > 0) {
                        animations.add(new Slide(from, new Vector2(squares_pos[y][x - 1][0], squares_pos[y][x - 1][1]), get_sprite(current_cell[0][1] == 1 ? blue_sprite : red_sprite, 1)));
                    }
                    if (x < board_size - 1) {
                        animations.add(new Slide(from, new Vector2(squares_pos[y][x + 1][0], squares_pos[y][x + 1][1]), get_sprite(current_cell[0][1] == 1 ? blue_sprite : red_sprite, 1)));
                    }
                    split_cells.add(new int[]{x, y, current_cell[0][1]});
                    new_board.set_at(x, y, board.get_split_at(x, y));
                } else {
                    new_board.clear_set_at(x, y, current_cell[0][0], current_cell[0][1]);
                }
            }
        }
        for (int[] pos : split_cells) {
            new_board.add(pos[0] + 1, pos[1], 1, pos[2]);
            new_board.add(pos[0] - 1, pos[1], 1, pos[2]);
            new_board.add(pos[0], pos[1] + 1, 1, pos[2]);
            new_board.add(pos[0], pos[1] - 1, 1, pos[2]);
        }
        if (animations.size() > 0) {
            wait_for_input = false;
            is_animating = true;
            animation_progress = 0f;
        } else {
            wait_for_input = true;
        }
    }

    private void stop_anim() {
        is_animating = false;
        // animation_progress = 0f;  // moved to animation setup
        board = new_board;
        new_board = new Board(board_size);
    }

    public float[] get_clear_color() {
        return players_bg[current_player];
    }

    public void render(SpriteBatch batch) {
        for (int x = 0; x < board_size; x++) {
            for (int y = 0; y < board_size; y++) {
                tile_sprite.setPosition(squares_pos[y][x][0], squares_pos[y][x][1]);
                tile_sprite.draw(batch);
            }
        }
        for (int x = 0; x < board_size; x++) {
            for (int y = 0; y < board_size; y++) {
                int[][] cell = board.get_at(x, y);
                if (cell[0][1] != 0) {
                    int amount = cell[0][0];
                    if (amount > 4 || amount < 0) {
                        amount = 0;
                    }
                    Sprite pawn = get_sprite(cell[0][1] == 1 ? blue_sprite : red_sprite, amount);
                    pawn.setPosition(squares_pos[y][x][0], squares_pos[y][x][1]);
                    pawn.draw(batch);
                }
            }
        }
        for (Animation anim : animations) {
            anim.render(batch, animation_progress);
        }
    }

}
