object get_uid_info {
  def get_info(line: String, Wanted_Object_Map: Map[String, Set[String]], robot_set: Set[String]): Map[String, String] = {
    var Out_Map: Map[String, String] = Map()
    var Tmp_Map: Map[String, com.alibaba.fastjson.JSONObject] = Map() //用来存放解析过的字符串
    try {
      var main_json = JSON.parseObject(line)
      val msg_type = main_json.getString("objectName")
      if (Wanted_Object_Map.contains(msg_type) && !robot_set.contains(main_json.getString("fromUserId"))) {
        Out_Map += ("objectName" -> msg_type)
        val wanted_elem = Wanted_Object_Map.get(msg_type).get // wanted_value: Set(msgTimestamp, toUserId, fromUserId, content:count)
        wanted_elem.foreach {
          elems => //elems 为content:count
            var first_flag: Int = 1
            var value: String = ""
            var new_elems: String = ""
            elems.split(":").foreach {
              elem => //elem 为content 或者下一个循环变成count
                if (first_flag == 1) {
                  value = main_json.getString(elem)
                  new_elems = elem
                  first_flag = 0
                } else {
                  if (!Tmp_Map.contains(new_elems)) {
                    Tmp_Map += (new_elems -> JSON.parseObject(value))
                  }
                  value = Tmp_Map.get(new_elems).get.getString(elem)
                  new_elems = new_elems + ":" + elem
                }
            }
            Out_Map += (elems -> value)
        }
      }
    } catch {
      case ex: Exception => ex.printStackTrace()
        Out_Map = Map()
    }
    Out_Map
  }
  def main(args: Array[String]): Unit = {}
}
