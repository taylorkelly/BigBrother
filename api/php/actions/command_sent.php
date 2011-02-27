<?php

class CommandSent extends Action
{
	public $removed = false;
	public function __construct($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return sprintf("%s %s at World %d - &lt;%d,%d,%d&gt;",$this->getUserLink(), $this->getActionString(),
		$this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'sent the command <i>&quot;'.htmlentities($this->data).'&quot;</i>';
	}
}