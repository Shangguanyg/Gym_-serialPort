package com.ganainy.motoman;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;

public class GraphicsOptimized implements Graphics {
	
	private Graphics g;
	private GL20 gl20;
	
	public GraphicsOptimized(Graphics g, GL20 gl20) {
		this.g = g;
		this.gl20 = gl20;
	}

    @Override
    public boolean isGL30Available() {
        return false;
    }

    @Override
	public GL20 getGL20() {
		return gl20;
	}

    @Override
    public GL30 getGL30() {
        return null;
    }

    @Override
    public void setGL20(GL20 gl20) {

    }

    @Override
    public void setGL30(GL30 gl30) {

    }

	@Override
	public int getWidth() {
		return g.getWidth();
	}

	@Override
	public int getHeight() {
		return g.getHeight();
	}

    @Override
    public int getBackBufferWidth() {
        return 0;
    }

    @Override
    public int getBackBufferHeight() {
        return 0;
    }

    @Override
    public float getBackBufferScale() {
        return 0;
    }

    @Override
    public int getSafeInsetLeft() {
        return 0;
    }

    @Override
    public int getSafeInsetTop() {
        return 0;
    }

    @Override
    public int getSafeInsetBottom() {
        return 0;
    }

    @Override
    public int getSafeInsetRight() {
        return 0;
    }

    @Override
    public long getFrameId() {
        return 0;
    }

    @Override
	public float getDeltaTime() {
		return g.getDeltaTime();
	}

	@Override
	public float getRawDeltaTime() {
		return g.getRawDeltaTime();
	}

	@Override
	public int getFramesPerSecond() {
		return g.getFramesPerSecond();
	}

	@Override
	public GraphicsType getType() {
		return g.getType();
	}

    @Override
    public GLVersion getGLVersion() {
        return null;
    }

    @Override
	public float getPpiX() {
		return g.getPpiX();
	}

	@Override
	public float getPpiY() {
		return g.getPpiY();
	}

	@Override
	public float getPpcX() {
		return g.getPpcX();
	}

	@Override
	public float getPpcY() {
		return g.getPpcY();
	}

	@Override
	public float getDensity() {
		return g.getDensity();
	}

	@Override
	public boolean supportsDisplayModeChange() {
		return g.supportsDisplayModeChange();
	}

    @Override
    public Monitor getPrimaryMonitor() {
        return null;
    }

    @Override
    public Monitor getMonitor() {
        return null;
    }

    @Override
    public Monitor[] getMonitors() {
        return new Monitor[0];
    }

    @Override
	public DisplayMode[] getDisplayModes() {
		return g.getDisplayModes();
	}

    @Override
    public DisplayMode[] getDisplayModes(Monitor monitor) {
        return new DisplayMode[0];
    }

    @Override
    public DisplayMode getDisplayMode() {
        return null;
    }

    @Override
    public DisplayMode getDisplayMode(Monitor monitor) {
        return null;
    }

    @Override
    public boolean setFullscreenMode(DisplayMode displayMode) {
        return false;
    }

    @Override
    public boolean setWindowedMode(int width, int height) {
        return false;
    }

	@Override
	public void setTitle(String title) {
		g.setTitle(title);
	}

    @Override
    public void setUndecorated(boolean undecorated) {

    }

    @Override
    public void setResizable(boolean resizable) {

    }

    @Override
	public void setVSync(boolean vsync) {
		g.setVSync(vsync);
	}

    @Override
    public void setForegroundFPS(int fps) {

    }

    @Override
	public BufferFormat getBufferFormat() {
		return g.getBufferFormat();
	}

	@Override
	public boolean supportsExtension(String extension) {
		return g.supportsExtension(extension);
	}

	@Override
	public void setContinuousRendering(boolean isContinuous) {
		g.setContinuousRendering(isContinuous);
	}

	@Override
	public boolean isContinuousRendering() {
		return g.isContinuousRendering();
	}

	@Override
	public void requestRendering() {
		g.requestRendering();
	}

	@Override
	public boolean isFullscreen() {
		return g.isFullscreen();
	}

    @Override
    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        return null;
    }

    @Override
    public void setCursor(Cursor cursor) {

    }

    @Override
    public void setSystemCursor(Cursor.SystemCursor systemCursor) {

    }

}
