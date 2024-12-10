package blue.endless.advent;

public interface Day {
	public String getSampleA();
	public default String getSampleB() { return getSampleA(); }
	public void a(String input);
	public void b(String input);
	public default boolean sameDataForB() { return true; }
}
