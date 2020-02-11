import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {
	double x, y; // 座標
	double v; // 進む速さ
	double angle; // 進む方向

	static final int EMBULLET = 3; // 弾の最大数
	EnemyBullet[] bullet = new EnemyBullet[EMBULLET]; // 弾の設定

	static final int HIT_RANGE = 16; // 当たり判定領域

	public Enemy(double x, double y, double angle) {

		for(int i = 0;i < EMBULLET;i++) {
			bullet[i] = new EnemyBullet();
		}

		this.x = x;
		this.y = y;
		this.angle = angle;
		v = 0.4;
	}

	public void move() {
		x += Math.cos(angle) * v; // X座標の更新
		y += Math.sin(angle) * v; // Y座標の更新

		if(x < 16 || x > Main.WIDTH - 16) { // 左右の壁に当たった場合
			angle = angle - Math.PI / 2; // 反射処理
		}
		else if(y < 16 || y > Main.HEIGHT - 16) { // 上下の壁に当たった場合
			angle = 2 * Math.PI - angle; // 反射処理
		}
	}

	public void draw(GraphicsContext gc) {
		gc.setFill(Color.BLUE); // 青色の設定
		gc.fillOval(x - 16, y - 16, 32, 32); // 形の設定
	}
}

class EnemyBullet {
	double x, y; // X・Y座標
	double v; // 速度
	double angle; // 進む方向
	boolean exist; // 存在フラグ

	static final int HIT_RANGE = 16; // 当たり判定領域

	public void enter(Enemy enemy) {
		exist = true; // 存在フラグをオンにする
		x = enemy.x; // 弾の初期座標、X
		y = enemy.y; // 弾の初期座標、Y
		angle = enemy.angle; // 弾の進行方向設定
		v = 0.6; // 弾の速度設定
	}

	public void move() {
		x += Math.cos(angle) * v; // X座標更新
		y += Math.sin(angle) * v; // Y座標更新

		if(x < 0 || x > Main.WIDTH || y < 0 || y > Main.HEIGHT) { // 画面外に出たら
			exist = false; // 弾を消す
		}
	}

	public void draw(GraphicsContext gc) {
		gc.setFill(Color.GREEN);
		gc.fillRect(x - 4, y - 4, 18, 18);
	}
}