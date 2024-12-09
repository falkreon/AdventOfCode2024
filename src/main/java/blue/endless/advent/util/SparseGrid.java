package blue.endless.advent.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SparseGrid<T> implements Grid<T> {
	private HashMap<Vec2i, T> cells = new HashMap<>();
	private T defaultValue = null;
	
	private boolean dirtyRect = true;
	private Rect rect = null;
	
	public SparseGrid() {
		
	}
	
	public SparseGrid(T defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	private void updateRect() {
		if (dirtyRect) {
			rect = new Rect(0,0,0,0);
			boolean first = true;
			
			for(Vec2i vec : cells.keySet()) {
				if (first) {
					first = false;
					rect = new Rect(vec.x(), vec.y(), 1, 1);
				} else {
					rect = rect.add(vec);
				}
			}
			
			dirtyRect = false;
		}
	}
	
	protected void markDirty() {
		dirtyRect = true;
	}
	
	public int getX() {
		updateRect();
		return rect.x();
	}
	
	public int getY() {
		updateRect();
		return rect.y();
	}
	
	@Override
	public int getWidth() {
		updateRect();
		return rect.width();
	}

	@Override
	public int getHeight() {
		updateRect();
		return rect.height();
	}
	
	public Rect getRect() {
		updateRect();
		return rect;
	}

	@Override
	public T get(Vec2i vec) {
		T result = cells.get(vec);
		return (result==null) ? defaultValue : result;
	}
	
	@Override
	public T get(int x, int y) {
		return get(new Vec2i(x,y));
	}

	@Override
	public void set(Vec2i vec, T value) {
		cells.put(vec, value);
		markDirty();
	}
	
	@Override
	public void set(int x, int y, T value) {
		set(new Vec2i(x,y), value);
	}
	
	public boolean isSet(Vec2i vec) {
		return cells.containsKey(vec);
	}
	
	public Set<Vec2i> occupiedCells() {
		return cells.keySet();
	}

	@Override
	public void clear(Vec2i vec) {
		T t = cells.remove(vec);
		if (t!=null) markDirty();
	}
	
	@Override
	public void clear(int x, int y) {
		clear(new Vec2i(x,y));
	}

	@Override
	public void clear() {
		cells.clear();
		markDirty();
	}
	
}