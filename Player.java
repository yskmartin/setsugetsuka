import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class Player {
	double x, y; // プレイヤーの座標

	Bullet[] bullet = new Bullet[BMAX]; // 弾の設定
	static final int BMAX = 10; // 弾の最大数
	static final int HRANGE = 8; // 当たり判定領域
	int hp; // プレイヤーのHP

	public Player() {
		for(int i = 0;i < BMAX;i++) { // Bulletのインスタンス化
			bullet[i] = new Bullet();
		}

		hp = 30; // プレイヤー最大HPの設定
		x = Main.WIDTH / 2; // 初期位置、X
		y = Main.HEIGHT * 3 / 3.5; // 初期位置、Y
	}

	public void move(byte[] key) {

		double mx, my; // 一時変数

		mx = x + (key[Main.KRIGHT] - key[Main.KLEFT]);
		my = y + (key[Main.DKEY ] - key[Main.UKEY  ]);

//		当たり判定
			if(!(mx < 16 || mx > Main.WIDTH - 16 || my < 16 || my > Main.HEIGHT - 16)) {
				x = mx; // X座標更新
				y = my; // Y座標更新
			}
	}

	public void draw(GraphicsContext gc) {
		gc.setFill(Color.CHARTREUSE); // プレイヤーの色設定
		gc.fillOval(x - 16, y - 16, 32, 32); // 自機を描画
		gc.fillText("HP: " + hp, x - 16, y - 16); // HPを表示
		gc.setFont(Font.font("SerifSerif", FontPosture.ITALIC, 38));
	}
}

class Bullet {
	double x, y; // 弾の座標
	double v; // 弾の速度
	boolean exist; // 弾の存在フラグ

	static final int HRANGE = 16; // 当たり判定領域

	public Bullet() {
		v = -1.0;
		exist = false;
	}

	public void enter(Player player) {
		exist = true;
		x = player.x;
		y = player.y;
	}

	public void move() {
		y += v;
		if(y + 16 < 0) { // 画面外に行った場合
			exist = false;
		}
	}

	public void draw(GraphicsContext gc) {
		gc.setFill(Color.YELLOW);
		gc.fillRect(x - 4, y - 16, 8, 32);
	}
}