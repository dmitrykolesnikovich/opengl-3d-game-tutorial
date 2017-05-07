package audio;

import java.io.IOException;

public abstract class Test {

	public static void main(String[] args) throws IOException, InterruptedException {

		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);

		int buffer = AudioMaster.loadSound("audio/bounce.wav");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);

		float xPos = 8;
		source.setPosition(xPos, 0, 1);
		float time = 0;
		

		char c = ' ';
		while (time < 5000) {
			
			xPos -= 0.03f;
			source.setPosition(xPos, 0, 1);
			Thread.sleep(10);
			time += 10;
		}

		source.delete();
		AudioMaster.cleanUp();

	}

}
