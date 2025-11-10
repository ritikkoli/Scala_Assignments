class Email(val address: String)

object dualNatureObject{
    def main(args:Array[String]):Unit={
        val e=Email("alice","mail.com")
        println(e)

        e match{
            case Email(user,domain)=> println(s"User: $user, Domain: $domain")
            case _=> println("Not a valid email")
        }
    }
}

object Email{
    def apply(user:String,domain:String):Email={
        new Email(s"$user@$domain")
    }
    def unapply(email:Email):Option[(String,String)]={
        val parts=email.address.split("@")
        if(parts.length==2) Some(parts(0),parts(1))
        else None
    }
}