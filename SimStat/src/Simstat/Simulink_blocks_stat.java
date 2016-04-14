package Simstat;

public class Simulink_blocks_stat {

	public String block_type;
	public Integer block_quantity;
	
	//getters
	public Integer getBlock_quantity() {
		return block_quantity;
	}
	public String getBlock_type() {
		return block_type;
	}
	
	//setters
	public void setBlock_quantity(Integer block_quantity) {
		this.block_quantity = block_quantity;
	}
	public void setBlock_type(String block_type) {
		this.block_type = block_type;
	}
}
