package io.dpteam.NoSmoking;

public class SmokePoint {
	String id;
	String world;
	int x;
	int y;
	int z;
	int effect_type;
	int direction;
	int rate;
	String text_param;
	String cr_name;
	boolean showpoint;

	public SmokePoint(String id, String wname, int x, int y, int z, int eff, int d, int rate, String cr_name, boolean showpoint) {
		super();
		this.id = id;
		this.world = wname;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = d;
		this.effect_type = eff;
		this.rate = rate;
		if (rate <= 0) {
			this.rate = 1;
		}

		if (rate > 10 && eff != 8 && eff != 9) {
			this.rate = 10;
		}

		this.text_param = "";
		this.cr_name = cr_name;
		this.showpoint = showpoint;
	}

	public SmokePoint(String id, String wname, int x, int y, int z, int eff, int d, int rate, String text_param, String cr_name, boolean showpoint) {
		super();
		this.id = id;
		this.world = wname;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = d;
		this.effect_type = eff;
		this.rate = rate;
		if (rate <= 0) {
			this.rate = 1;
		}

		if (rate > 10 && eff != 8 && eff != 9) {
			this.rate = 10;
		}

		this.text_param = text_param;
		this.cr_name = cr_name;
		this.showpoint = showpoint;
	}
}
