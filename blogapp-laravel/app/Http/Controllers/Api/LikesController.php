<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Like;
use Illuminate\Support\Facades\Auth;

class LikesController extends Controller
{
    public function like(Request $request){
        $like = Like::where('post_id',$request->id)->where('user_id',Auth::user()->id)->get();
        // 0 döndürüp döndürmediğini kontrol et, o zaman bu gönderi beğenilmiyor
        if(count($like)>0){
            //birden fazla beğeni alamayız
            $like[0]->delete();
            return response()->json([
                'success' => true,
                'message' => 'unliked'
            ]);
        }
        $like = new Like;
        $like->user_id = Auth::user()->id;
        $like->post_id = $request->id;
        $like->save();

        return response()->json([
            'success' => true,
            'message' => 'liked'
        ]);
    }
}
