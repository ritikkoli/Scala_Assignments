package library.users

import library.items.ItemType2

class Member2(val name: String) {
  def borrowItem(item: ItemType2): Unit = {
    println(s"$name borrows '${item.title}'")
  }
}
