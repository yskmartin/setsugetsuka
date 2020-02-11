import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.util.Duration;

//当たり判定・アニメーション実装
public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	static final int WIDTH = 800; // キャンバスウィンドウの幅
	static final int HEIGHT = 700; // キャンバスウィンドウの高さ

	Player player; // Playerクラスのインスタンス作成
	double x, y; // プレイヤーの座標

	// 移動キーの設定
	static final int KRIGHT = 0;
	static final int KLEFT = 1;
	static final int UKEY = 2;
	static final int DKEY = 3;
	static final int ZKEY = 4;
	byte[] key = new byte[5];

	static int QENEMY = 2; // 敵の数
	Enemy[] enemy = new Enemy[QENEMY]; // 敵

	long cnt; // カウンタ変数
	int score; // スコアの設定

	@Override

	public void start(Stage stage) {

		Canvas canvas1 = new Canvas(WIDTH, HEIGHT); // Canvas1の設定
		Canvas canvas2 = new Canvas(WIDTH, HEIGHT); // Canvas2の設定
		GraphicsContext gc1 = canvas1.getGraphicsContext2D(); // GraphicsContextを取得
		GraphicsContext gc2 = canvas2.getGraphicsContext2D();

		contsq(gc2);

		// ボタンの作成
		Button wte = new Button("White Mode");
		Button drk = new Button("Dark Mode");

		HBox hbox = new HBox();
		hbox.getChildren().addAll(wte,drk);
		hbox.setAlignment(Pos.CENTER);

		BorderPane layer1 = new BorderPane();// layer1設定
		BorderPane.setAlignment(hbox, Pos.CENTER);

		layer1.setBottom(hbox);
		layer1.setCenter(canvas1);

		//背景図形作成
		Rectangle rec1 = new Rectangle();
		rec1.setWidth(100);
		rec1.setHeight(100);
		rec1.setArcWidth(100);
		rec1.setArcHeight(100);
		rec1.setStroke(Color.DODGERBLUE); // 境界線の色をRGBで設定
		rec1.setStrokeWidth(0.8); // 境界線の幅を設定
		rec1.setFill(Color.TRANSPARENT);


		Sphere sph = new Sphere(80);// 球設定

        PhongMaterial material = new PhongMaterial();// 球色設定
        material.setDiffuseColor(Color.YELLOW);
        material.setSpecularColor(Color.ORANGE);
        sph.setMaterial(material);

        Pane layer2 = new Pane();// layer2設定
		layer2.getChildren().addAll(rec1);

		Group root = new Group();

		root.getChildren().addAll(sph,layer1,canvas2,layer2);

		Scene scene = new Scene(root); // Sceneの設定
		stage.setTitle("イベント学習"); //タイトルの設定
		stage.setScene(scene);
		stage.show();//ステージ表示

		wte.setOnAction((ActionEvent event) ->{
			layer1.setStyle("-fx-background-color: lightblue;");// 背景色冬色に変更
		});
		drk.setOnAction((ActionEvent event) ->{
			layer1.setStyle("-fx-background-color: black;");// 背景色ダークモードに変更
		});

		player = new Player(); // Playerのインスタンス化

		for (int i = 0; i < QENEMY; i++) {
			enemy[i] = new Enemy(WIDTH * (i + 1) / (QENEMY + 1), // X座標設定
					16, // Y座標設定
					Math.PI - Math.PI * (i + 1) / (QENEMY + 1) // angle設定
			);
		}

		Thread thread = new Thread(() -> { // メインスレッド
			while (true) {
				gc1.clearRect(0, 0, WIDTH, HEIGHT); // 全画面消去

				player.move(key); // プレイヤー動作
				player.draw(gc1); // プレイヤー描画

				for (int i = 0; i < Player.BMAX; i++) {// Bulletクラス呼び出し
					if (player.bullet[i].exist) {
						player.bullet[i].move();
						player.bullet[i].draw(gc1);
					}
				}

				if (cnt % 60 == 0) { // 60フレームごとに発射
					if (key[ZKEY] == 1) { // Zキーが押されたら
						for (int i = 0; i < Player.BMAX; i++) {
							if (!player.bullet[i].exist) { // 存在していなかったら
								player.bullet[i].enter(player); // 発射
								break;
							}
						}
					}
				}

				for (int i = 0; i < QENEMY; i++) { // Enemyクラス呼び出し
					enemy[i].move();
					enemy[i].draw(gc1);
				}

				if (cnt % 30 == 0) { // 敵の弾、登録30フレームずつ
					for (int i = 0; i < QENEMY; i++) {
						for (int j = 0; j < Enemy.EMBULLET; j++) {
							if (!enemy[i].bullet[j].exist) { // 弾が存在してい場合
								enemy[i].bullet[j].enter(enemy[i]);
								break;
							}
						}
					}
				}

				for (int i = 0; i < QENEMY; i++) { //敵の弾を移動・描画
					for (int j = 0; j < Enemy.EMBULLET; j++) {
						if (enemy[i].bullet[j].exist) { // 弾が存在する条件
							enemy[i].bullet[j].move();
							enemy[i].bullet[j].draw(gc1);
						}
					}
				}

				cnt++; // カウントアップ

				try {
					Thread.sleep(2); // 2ms休止
				} catch (Exception e) {
				}

				for (int i = 0; i < Player.BMAX; i++) { // 当たり判定：プレイヤーの弾と敵
					if (player.bullet[i].exist) {
						for (int j = 0; j < QENEMY; j++) {
							double dx, dy; // 弾と敵の距離（X座標、Y座標）
							double r; // 弾と敵の直線距離

							dx = player.bullet[i].x - enemy[j].x;
							dy = player.bullet[i].y - enemy[j].y;
							r = Math.sqrt(dx * dx + dy * dy); // 直線距離を計算（三平方の定理）
							if (r < Bullet.HRANGE + Enemy.HIT_RANGE) {
								Random random = new Random();
								enemy[j].x = 16 + random.nextInt(Main.WIDTH - 32); // Ｘ座標をランダムな値で設定
								enemy[j].y = 16; // Y座標を初期位置に戻す
								score += 100; // スコアを加算
								QENEMY -= 1;
							}
						}
					}
				}

				for (int i = 0; i < QENEMY; i++) { // 当たり判定：プレイヤーと敵の弾
					for (int j = 0; j < Enemy.EMBULLET; j++) {
						if (enemy[i].bullet[j].exist) {
							double dx, dy; // 弾と敵の距離（X座標、Y座標）
							double r; // 弾と敵の直線距離

							dx = player.x - enemy[i].bullet[j].x;
							dy = player.y - enemy[i].bullet[j].y;
							r = Math.sqrt(dx * dx + dy * dy); // 直線距離を計算（三平方の定理）
							if (r < Player.HRANGE + EnemyBullet.HIT_RANGE) {
								enemy[i].bullet[j].exist = false; // 弾を消す
								player.hp--; // HPを１削る
							}
						}
					}
				}

				for (int i = 0; i < QENEMY; i++) { // 当たり判定：プレイヤーと敵
					double dx, dy; // 弾と敵の距離（X座標、Y座標）
					double r; // 弾と敵の直線距離

					dx = player.x - enemy[i].x;
					dy = player.y - enemy[i].y;
					r = Math.sqrt(dx * dx + dy * dy); // 直線距離を計算（三平方の定理）
					if (r < Player.HRANGE + Enemy.HIT_RANGE) {
						player.hp-=1; // HPを１削る
					}
				}

				if (player.hp == 0) { // プレイヤーのHPが０の場合
					System.out.println("SCORE: " + score); // 最終スコアを表示
					System.exit(0); // ゲーム終了
				}else if(QENEMY == 0){
					System.out.println("Stage Clear!!"); // ステージクリア表示
					System.out.println("SCORE: " + score); // スコアの表示
					System.exit(0); // ゲーム終了
				}
			}
		});
		thread.setDaemon(true); // スレッドのデーモン化
		thread.start(); // メインループを開始

		scene.setOnKeyPressed(e -> { //キー操作制御
			switch (e.getCode()) {
			case RIGHT:
				key[KRIGHT] = 1;
				break;
			case LEFT:
				key[KLEFT] = 1;
				break;
			case UP:
				key[UKEY] = 1;
				break;
			case DOWN:
				key[DKEY] = 1;
				break;
			case Z:
				key[ZKEY] = 1;
				break;
			default:
				break;
			}
		});

		scene.setOnKeyReleased(e -> { // キー離上時の処理
			switch (e.getCode()) {
			case RIGHT:
				key[KRIGHT] = 0;
				break;
			case LEFT:
				key[KLEFT] = 0;
				break;
			case UP:
				key[UKEY] = 0;
				break;
			case DOWN:
				key[DKEY] = 0;
				break;
			case Z:
				key[ZKEY] = 0;
				break;
			default:
				break;
			}
		});

		//遷移効果設定
        FadeTransition fdtrans = new FadeTransition(Duration.millis(5000),sph);
        fdtrans.setFromValue(0.1f);//フェード元
        fdtrans.setToValue(1.0f);//フェード先
        fdtrans.setCycleCount(1);//何サイクルか
        fdtrans.setAutoReverse(false);//繰返し設定

        TranslateTransition trtrans = new TranslateTransition(Duration.millis(5000), sph);
        trtrans.setFromX(700);//X 移動元の位置を設定
        trtrans.setToX(0);//X 移動先の位置を設定
        trtrans.setFromY(80);//Y 移動元の位置を設定
        trtrans.setToY(1800);//Y 移動先の位置を設定
        trtrans.setCycleCount(1);
        trtrans.setAutoReverse(false);

        ScaleTransition sctrans = new ScaleTransition(Duration.millis(5000), sph);
        sctrans.setFromX(0.5);//元スケール
        sctrans.setFromY(0.5);
        sctrans.setToX(10.0);//先スケール
        sctrans.setToY(10.0);
        sctrans.setCycleCount(1);
        sctrans.setAutoReverse(false);

        ParallelTransition paratrans = new ParallelTransition();
        paratrans.getChildren().addAll(fdtrans,trtrans,sctrans);
        paratrans.setCycleCount(1);
        paratrans.play();


		//パス遷移
		Path path = new Path();
		path.getElements().add(new MoveTo(20,20));
		path.getElements().add(new CubicCurveTo(320, 180, 320, 360, 80, 880));

		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(10000));
		pathTransition.setPath(path);
		pathTransition.setNode(rec1);
		pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		pathTransition.setCycleCount(Timeline.INDEFINITE);
		pathTransition.setAutoReverse(false);
		pathTransition.play();

	}

	public void contsq(GraphicsContext gc){
		gc.setStroke(Color.FLORALWHITE);
		gc.translate(200,180);// .translate(横の距離,縦の距離)初期位置
		for(int i=0;i<50;i++) {
			gc.strokeRect(50, 50, 100, 100);// strokeRect(double x, double y, double w, double h)
			gc.rotate(20);
			gc.scale(0.97,0.97);

		}
	}
}