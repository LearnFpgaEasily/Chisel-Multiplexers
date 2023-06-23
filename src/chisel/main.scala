import chisel3._
import chisel3.util._

class MuxBundle extends Bundle{
    val a = Input(Bool())
    val b = Input(Bool())
    val select = Input(Bool())
    val out = Output(Bool())
}

class MuxExample extends Module{
    val io = IO(new MuxBundle)

    io.out := Mux(io.select.asBool, io.a, io.b)
}

class MuxCaseBundle extends Bundle{
    val switch1 = Input(Bits(1.W))
    val switch2 = Input(Bits(1.W))
    val out = Output(UInt(2.W))
}
class MuxCaseExample extends Module{
    val io = IO(new MuxCaseBundle)

    val my_cases = Seq(io.switch1.asBool -> 1.U, io.switch2.asBool->2.U)
    io.out := MuxCase(0.U, my_cases)
}

class MuxLookUpBundle extends Bundle{
    val index = Input(UInt(3.W))
    val out   = Output(UInt(8.W))
}
class MuxLookupExample extends Module{
    val io = IO(new MuxLookUpBundle)

    val my_cases = Seq.tabulate(8)(idx =>(idx.U -> (1.U<<idx)))
    io.out := MuxLookup(io.index, 0.U(8.W), my_cases)
}

class Mux1hBundle extends Bundle{
    val switches = Input(Bits(2.W))
    val out      = Output(Bits(2.W))
}
class Mux1hExample extends Module{
    val io = IO(new Mux1hBundle)

    val my_cases = Seq(
        io.switches(0) -> 1.U,
        io.switches(1) -> 2.U)
    io.out := Mux1H(my_cases)
}



class AlchitryCUTop extends Module {
    val io_mux       = IO(new MuxBundle)
    val io_muxcase   = IO(new MuxCaseBundle)
    val io_muxlookup = IO(new MuxLookUpBundle)
    val io_mux1H     = IO(new Mux1hBundle)
    
    // the alchitry CU board has an active low reset
    val reset_n = !reset.asBool

    withReset(reset_n){
        val my_mux         = Module(new MuxExample)
        val my_muxcase     = Module(new MuxCaseExample)
        val my_muxlookup   = Module(new MuxLookupExample)
        val my_mux1h       = Module(new Mux1hExample)

        io_mux       <> my_mux.io
        io_muxcase   <> my_muxcase.io
        io_muxlookup <> my_muxlookup.io
        io_mux1H     <> my_mux1h.io
    }
}

object Main extends App{
    (new chisel3.stage.ChiselStage).emitVerilog(new AlchitryCUTop, Array("--target-dir", "build/artifacts/netlist/"))
}