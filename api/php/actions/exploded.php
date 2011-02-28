<?php

class Exploded extends Action
{
	private $detType=0;
	public function __construct($row,$type)
	{
		$this->detType=$type;
		parent::LoadData($row);
	}
	public function getType()
	{
		switch($this->detType)
		{
			case 0: return 'A Creeper'; break;
			case 1: return 'TNT'; break;
			default: return 'Something'; break;
		}
	}
	public function __toString()
	{
		return sprintf("%s exploded at World %d - &lt;%d,%d,%d&gt;",$this->getActionString(), $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return $this->getType().' exploded';
	}
}