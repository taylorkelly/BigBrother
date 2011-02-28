<?php

class ChestChanged extends Action
{
	public $removed = false;
	public function __construct($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return sprintf("%s changed chest at World %d - &lt;%d,%d,%d&gt;",$this->getUserLink(), $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'changed chest (D: '.$this->data.'; T: '.$this->type.')';
	}
}