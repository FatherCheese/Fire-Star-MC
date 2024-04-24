package jamdoggie.firestarmc.multiworld;

public class RegisteredMultiWorld
{
	public String name;
	public long seed;
	public int dimensionId;

	public RegisteredMultiWorld(String name, long seed, int dimensionId)
	{
		this.name = name;
		this.seed = seed;
		this.dimensionId = dimensionId;
	}
}
