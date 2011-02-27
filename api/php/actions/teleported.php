<?php

/** Sign Modified Action

*/

class Teleported extends Action
{
	public $removed = false;
	public function Teleported($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return sprintf("%s teleported at World %d - &lt;%d,%d,%d&gt;",$this->getUserLink(), $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'teleported';
	}
}