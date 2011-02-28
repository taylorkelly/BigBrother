<?php
/** bbStatsPlus Action Processing Class

Blah blah blah BSD License.
*/

require('actions/block_modified.php');
require('actions/button_pressed.php');
require('actions/chatted.php');
require('actions/chest_changed.php');
require('actions/chest_opened.php');
require('actions/command_sent.php');
require('actions/disconnected.php');
require('actions/door_opened.php');
require('actions/exploded.php');
require('actions/fire_lit.php');
require('actions/lever_switched.php');
require('actions/logged_in.php');
require('actions/sign_modified.php');
require('actions/teleported.php');
require('actions/tnt_exploded.php');

class Action
{
	public $id=0;
	public $date=0;
	public $player='';
	public $world=0;
	public $X=0; 	
	public $Y=0; 	
	public $Z=0;
	public $type=0;
	public $data='';
	public $rbacked=false;
	
	// 0 id 
	// 1 date
	// 2 player
	// 3 action
	// 4 world
	// 5 x
	// 6 y
	// 7 z
	// 8 type
	// 9 data
	// 10 rbacked
	public function LoadData($row){
		$this->id=intval($row[0]);
		$this->date=intval($row[1]);
		$this->player=$row[2];
		$this->actionID=intval($row[3]);
		$this->world=$actionID=intval($row[4]);
		$this->X=intval($row[5]); 	
		$this->Y=intval($row[6]); 	
		$this->Z=intval($row[7]);
		$this->type=intval($row[8]);
		$this->data=$row[9];
		$this->rbacked=intval($row[10])==1;
	}
	
	public static function FromData($row)
	{
		
		$aID=intval($row[3]);
		$act=null;
		switch($aID)
		{
		case BLOCK_BROKEN: 
			$act=new BlockModified($row,true);
		break;
		case BLOCK_PLACED:
			$act=new BlockModified($row,false);
		break;
		case DESTROY_SIGN_TEXT:
			$act=new SignModified($row,true);
		break;
		case TELEPORT:
			$act=new Teleported($row);
		break;
		case DELTA_CHEST:
			$act=new ChestChanged($row);
		break;
		case COMMAND:
			$act=new CommandSent($row);
		break;
		case CHAT:
			$act=new Chatted($row);
		break;
		case DISCONNECT:
			$act=new Disconnected($row);
		break;
		case LOGIN:
			$act=new LoggedIn($row);
		break;
		case DOOR_OPEN:
			$act=new DoorOpened($row);
		break;
		case BUTTON_PRESS:
			$act=new ButtonPressed($row);
		break;
		case LEVER_SWITCH:
			$act=new LeverSwitched($row);
		break;
		case CREATE_SIGN_TEXT:
			$act=new SignModified($row,false);
		break;
		/*
		case LEAF_DECAY:
			$act=new LeavesDecayed($row);
		break;
		*/
		case FLINT_AND_STEEL:
			$act=new FireLit($row);
		break;
		case TNT_EXPLOSION:
			$act=new Exploded($row,1);
		break;
		case CREEPER_EXPLOSION:
			$act=new Exploded($row,0);
		break;
		case MISC_EXPLOSION:
			$act=new Teleport($row,-1);
		break;
		case OPEN_CHEST:
			$act=new ChestOpened($row);
		break;
		case BLOCK_BURN:
			$act=new BlockBurnt($row);
		break;
		}
		return $act;
	}
	
	public static function toWidget($stats)
	{
		$o='<table><thead><th>Time</th><th>Player</th><th>Action</th><th>World</th><th>X</th><th>Y</th><th>Z</th></thead>';
		$i=0;
		foreach($stats as $stat)
		{
			$o.='<tr class="'.(($i++%2==0)?'tr_even':'tr_odd').' act_'.$stat->actionID.'">';
			$o.='<td>'.date('M.d@h:i:s A',$stat->date).'</td>';
			$o.='<td>'.$stat->getUserLink().'</td>';
			$o.='<td>'.$stat->getActionString().'</td>';
			$o.='<td>'.$stat->getWorldName().'</td>';
			$o.='<td>'.$stat->X.'</td>';
			$o.='<td>'.$stat->Y.'</td>';
			$o.='<td>'.$stat->Z.'</td>';
			$o.='</tr>';
		}
		$o.='</table>';
		return $o;
	}
	
	public function getActionString()
	{
		return $this->actionID.' doesn\'t have an action string yet!';
	}
	
	public function getWorldName()
	{
		switch($this->world)
		{
			case 0:
				return 'Normal';
			case 1:
				return 'Nether';
			default:
				return $this->world;
		}
	}
	
	public function getUserLink()
	{
		return '<a href="player.php?'.$this->player.'">'.$this->player.'</a>';
	}
	
	public function getPos()
	{
		return sprintf('World %d - &lt;%d,%d,%d&gt;',$this->world,$this->X,$this->Y,$this->Z);
	}
}