<?php

class Disconnected extends Action
{
	public function __construct($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return $this->getUserLink().' disconnected at '.$this->getPos();
	}
	
	public function getActionString()
	{
		return 'disconnected';
	}
}