<?php

class Chatted extends Action
{
	public function __construct($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return sprintf("%s said &quot;%s&quot; at World %d - &lt;%d,%d,%d&gt;",$this->getUserLink(),htmlentities($this->data), $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'said <i>&quot;'.htmlentities($this->data).'&quot;</i>';
	}
}