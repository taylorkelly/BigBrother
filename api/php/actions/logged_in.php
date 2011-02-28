<?php

class LoggedIn extends Action
{
	public function __construct($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return sprintf("%s logged in at World %d - &lt;%d,%d,%d&gt;",$this->getUserLink(),$this->data, $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'logged in';
	}
}