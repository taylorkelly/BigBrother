<?php

/** Sign Modified Action

*/

class SignModified extends Action
{
	public $removed = false;
	public function SignModified($row,$wasRemoved)
	{
		parent::LoadData($row);
		$this->removed=$wasRemoved;
	}
	
	public function __toString()
	{
		return sprintf("%s %s a sign with text %s at World %d - &lt;%d,%d,%d&gt;",$this->getUserLink(),($this->wasRemoved) ? 'removed' : 'placed', $this->data, $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return (($this->wasRemoved) ? 'removed' : 'placed').' a sign with text <i>&quot;'.$this->data.'&quot;</i>';
	}
}